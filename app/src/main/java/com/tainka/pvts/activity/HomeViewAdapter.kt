package com.tainka.pvts.activity

import android.content.Intent
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
import java.net.URL
import kotlin.concurrent.thread

class HomeViewAdapter : RecyclerView.Adapter<HomeViewAdapter.HomeViewHolder>() {

    private val listMovieCard = ArrayList<DataMovie>()
    private var mainMenuActivity = MainMenuActivity()

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

        fun bind(movie : DataMovie)
        {
            binding.movieTitle.text = movie.title
            binding.movieImage.setBackgroundResource(R.drawable.loading_animation)
            val animation = binding.movieImage.background as AnimationDrawable

            data = movie

            animation.start()

            thread {
                var success : Boolean = false

                while(!success)
                {
                    try {
                        var url = URL("http://192.168.100.8/" + movie.url + "/poster")

                        var poster = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                        animation.stop()

                        mainMenuActivity.runOnUiThread() {
                            binding.movieImage.setImageBitmap(poster)
                        }

                        success = true
                    }
                    catch (e : IOException)
                    {

                    }
                }
            }

            binding.movieCard.setOnClickListener {
                mainMenuActivity.apply {
                    this.processPage(data)
                }
            }

            Log.d("bind", data.toString())
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