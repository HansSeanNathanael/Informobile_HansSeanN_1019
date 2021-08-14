package com.tainka.pvts.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tainka.pvts.R
import com.tainka.pvts.data.DataEpisodes
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.data.DataSeasons
import com.tainka.pvts.databinding.ActivityEpisodePageBinding
import com.tainka.pvts.utilities.JSONEncodeParser
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class EpisodePageActivity : AppCompatActivity() {

    lateinit var binding : ActivityEpisodePageBinding

    lateinit var episodeCardAdapter : EpisodeViewAdapter

    lateinit var dataMovie : DataMovie
    lateinit var dataSeasons : DataSeasons
    var seasonPosition : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEpisodePageBinding.inflate(layoutInflater)

        dataMovie = intent.getSerializableExtra("movie") as? DataMovie ?: DataMovie()
        dataSeasons = intent.getSerializableExtra("season") as? DataSeasons ?: DataSeasons()
        seasonPosition = intent.getIntExtra("season_position", 0)

        binding.moviePoster.setBackgroundResource(R.drawable.loading_animation)
        (binding.moviePoster.background as AnimationDrawable).start()
        thread {
            loadPoster()
        }

        episodeCardAdapter = EpisodeViewAdapter(this)
        binding.episodeCardList.apply {
            layoutManager = LinearLayoutManager(this@EpisodePageActivity, LinearLayoutManager.VERTICAL, false)
            adapter = episodeCardAdapter
        }

        binding.movieTitle.text = dataMovie.title

        if (dataSeasons.season_id != -1)
        {
            binding.season.text = "Season: $seasonPosition/${dataMovie.seasonAmount}"
            binding.totalEpisode.text = "Total episode: ${dataSeasons.totalEpisode}"

            thread {
                val listItem = getEpisodeData()
                runOnUiThread {
                    episodeCardAdapter.setListItem(listItem)
                }
            }
        }
        else
        {
            thread {
                getSeasonData()
                runOnUiThread {
                    binding.season.text = "Season: $seasonPosition/${dataMovie.seasonAmount}"
                    binding.totalEpisode.text = "Total episode: ${dataSeasons.totalEpisode}"
                }

                val listItem = getEpisodeData()
                runOnUiThread {
                    episodeCardAdapter.setListItem(listItem)
                }
            }
        }



        setContentView(binding.root)
    }

    private fun getSeasonData()
    {
        var result = JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?season_from_movie_id=${dataMovie.id}")
        if (result.size == 1 && result[0] is List<*>)
        {
            var data = result[0]
            with(data as List<String>)
            {
                dataSeasons.changeData(data[0].toInt(), data[3], data[5], data[4].toInt())
            }
        }
    }

    private fun getEpisodeData() : List<DataEpisodes>
    {
        val returnValue : MutableList<DataEpisodes> = mutableListOf()

        val result = JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?season_id=${dataSeasons.season_id}")

        for (i in result)
        {
            if (i is List<*> && i.size == 3)
            {
                with(i as List<String>)
                {
                    returnValue.add(
                        DataEpisodes(i[0].toInt(), i[1], i[2])
                    )
                }
            }
        }

        return returnValue
    }

    private fun loadPoster()
    {
        while (true)
        {
            try
            {
                val connection = URL("http://192.168.100.8/${dataMovie.url}/poster")

                var image = BitmapFactory.decodeStream(connection.openConnection().getInputStream())

                runOnUiThread {
                    (binding.moviePoster.background as AnimationDrawable).stop()
                    binding.moviePoster.setImageBitmap(image)
                }
                break
            }
            catch (e : IOException)
            {

            }
        }
    }

    fun processPage(dataEpisodes: DataEpisodes, position : Int)
    {
        val intent = Intent(this@EpisodePageActivity, VideoPageActivity::class.java)
        intent.putExtra("movie", dataMovie)
        intent.putExtra("season", dataSeasons)
        intent.putExtra("episode", dataEpisodes)
        intent.putExtra("episode_position", position)
        intent.putExtra("season_position", seasonPosition)
        startActivity(intent)
    }
}