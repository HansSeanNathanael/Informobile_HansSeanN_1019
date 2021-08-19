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

class CardListAdapter(var cardListActivity  : VideoCardListActivity) : RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    private val listMovieCard = ArrayList<DataMovie>()
    private val listImagePoster = ArrayList<Bitmap?>()
    private var loadPosterThread = thread(false) {}

    fun setParentActivity(parentActivity : VideoCardListActivity)
    {
        cardListActivity = parentActivity
    }

    fun setList(list : List<DataMovie>)
    {
        listMovieCard.clear()
        listMovieCard.addAll(list)

        loadPosterThread.interrupt()

        for (i in listImagePoster.indices)
        {
            listImagePoster[i]?.recycle()
        }
        listImagePoster.clear()

        for (i in listMovieCard.indices)
        {
            listImagePoster.add(null)
        }

        loadPosterThread = thread {
            var success : Boolean
            var interrupted = false
            for (i in listMovieCard.indices)
            {
                try
                {
                    success = false
                    while (!success)
                    {
                        try
                        {
                            val connection = URL("${cardListActivity.getString(R.string.server)}/${listMovieCard[i].url}/poster").openConnection()
                            val poster = BitmapFactory.decodeStream(connection.getInputStream())

                            listImagePoster[i] = poster
                            success = true
                            break
                        }
                        catch (e : IOException)
                        {
                            if (e is InterruptedIOException)
                            {
                                interrupted = true
                                break
                            }
                            if (e is NoSuchFileException)
                            {
                                break
                            }
                        }
                    }
                    if (interrupted)
                    {
                        throw InterruptedException()
                    }
                }
                catch (e : InterruptedException)
                {
                    break
                }

                if (success)
                {

                    cardListActivity.runOnUiThread {
                        notifyItemChanged(i)
                    }
                }
            }
        }



        notifyDataSetChanged()
    }

    inner class CardViewHolder(private val binding: MovieCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        lateinit var data : DataMovie

        fun bind(movie : DataMovie, position: Int)
        {
            binding.movieTitle.text = movie.title

            data = movie

            if (listImagePoster[position] != null)
            {
                binding.movieImage.setImageBitmap(listImagePoster[adapterPosition])
            }
            else
            {
                binding.movieImage.setBackgroundResource(R.drawable.loading_animation)
                binding.movieImage.setImageResource(android.R.color.transparent)
                val animation = binding.movieImage.background as AnimationDrawable
                animation.start()
            }

            binding.movieCard.setOnClickListener {
                cardListActivity.apply {
                    if (data.id != -1)
                    {
                        this.processPage(data)
                    }
                }
            }

            //Log.d("Thread size", Thread.getAllStackTraces().size.toString())

            //Log.d("bind", data.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemBinding = MovieCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CardViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val movieData = listMovieCard[position]
        holder.bind(movieData, position)
    }

    override fun getItemCount() : Int {
        return  listMovieCard.size
    }

}