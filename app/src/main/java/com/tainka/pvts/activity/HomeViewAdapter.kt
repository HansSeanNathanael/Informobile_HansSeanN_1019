package com.tainka.pvts.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tainka.pvts.R
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.databinding.MovieCardBinding
import java.io.IOException
import java.io.InterruptedIOException
import java.lang.NullPointerException
import java.net.URL
import kotlin.concurrent.thread

class HomeViewAdapter(var mainMenuActivity : MainMenuActivity) : RecyclerView.Adapter<HomeViewAdapter.HomeViewHolder>() {

    private val listMovieCard = ArrayList<DataMovie>()

    fun setParentActivity(parentActivity : MainMenuActivity)
    {
        mainMenuActivity = parentActivity
    }

    fun setList(list : List<DataMovie>)
    {
        listMovieCard.clear()
        listMovieCard.addAll(list)

        notifyDataSetChanged()
    }

    inner class HomeViewHolder(private val binding: MovieCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        lateinit var data : DataMovie
        private var threadLoadPoster = thread(false) {}
        private var poster : Bitmap = Bitmap.createBitmap(96, 128, Bitmap.Config.ARGB_8888)

        fun bind(movie : DataMovie)
        {
            binding.movieTitle.text = movie.title

            if (binding.movieImage.background !is AnimationDrawable)
            {
                binding.movieImage.setBackgroundResource(R.drawable.loading_animation)
                val animation = binding.movieImage.background as AnimationDrawable
                animation.start()
            }

            data = movie

            if (threadLoadPoster.isAlive)
            {
                threadLoadPoster.interrupt()
            }

            threadLoadPoster = thread {

                var success = false
                while(!success)
                {
                    try
                    {
                        val bitmapStream = URL("${mainMenuActivity.getString(R.string.server)}/${movie.url}/poster").openConnection().getInputStream()

                        val newPoster : Bitmap?

                        try
                        {
                            newPoster =  BitmapFactory.decodeStream(bitmapStream)
                        }
                        catch (e : InterruptedIOException)
                        {
                            break
                        }
                        catch (e : NullPointerException)
                        {
                            break
                        }

                        if (Thread.currentThread().isInterrupted)
                        {
                            break
                        }

                        if (binding.movieImage.background is AnimationDrawable)
                        {
                            (binding.movieImage.background as AnimationDrawable).stop()
                        }

                        val oldPoster = poster

                        mainMenuActivity.runOnUiThread {
                            if (newPoster != null)
                            {
                                poster = newPoster
                                oldPoster.recycle()
                                binding.movieImage.setImageBitmap(poster)
                            }
                        }


                        success = true
                        break
                    }
                    catch (e : IOException)
                    {
                        if (e is InterruptedIOException)
                        {
                            break
                        }
                        else if (e is NoSuchFileException)
                        {
                            break
                        }
                    }
                }
            }

            binding.movieCard.setOnClickListener {
                mainMenuActivity.apply {
                    this.processPage(data)
                }
            }

            //Log.d("Thread size", Thread.getAllStackTraces().size.toString())

            //Log.d("bind", data.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemBinding = MovieCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HomeViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val movieData = listMovieCard[position]
        holder.bind(movieData)
    }

    override fun getItemCount() : Int {
        return  listMovieCard.size
    }

}