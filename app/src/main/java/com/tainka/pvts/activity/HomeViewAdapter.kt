package com.tainka.pvts.activity

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tainka.pvts.R
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.databinding.ActivityMainMenuBinding
import com.tainka.pvts.databinding.MovieCardBinding
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class HomeViewAdapter : RecyclerView.Adapter<HomeViewAdapter.HomeViewHolder>() {

    val listMovieCard = ArrayList<DataMovie>()
    var mainMenuActivity = MainMenuActivity()

    fun setList(list : List<DataMovie>)
    {
        listMovieCard.clear()
        listMovieCard.addAll(list)

        notifyDataSetChanged()
    }

    inner class HomeViewHolder(private val binding: MovieCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(movie : DataMovie)
        {
            binding.movieTitle.text = movie.title
            binding.movieImage.setBackgroundResource(R.drawable.loading_animation)
            val animation = binding.movieImage.background as AnimationDrawable

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