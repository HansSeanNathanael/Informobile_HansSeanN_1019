package com.tainka.pvts.activity

import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.tainka.pvts.R
import com.tainka.pvts.data.DataEpisodes
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.data.DataSeasons
import com.tainka.pvts.databinding.ActivityVideoPageBinding
import com.tainka.pvts.utilities.JSONEncodeParser
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class VideoPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVideoPageBinding

    private var seasonPosition = 0
    private var episodePosition = 0

    private lateinit var dataSeasons : DataSeasons
    private lateinit var dataMovie : DataMovie
    private lateinit var dataEpisode : DataEpisodes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPageBinding.inflate(layoutInflater)

        dataMovie = intent.getSerializableExtra("movie") as DataMovie
        dataSeasons = intent.getSerializableExtra("season") as? DataSeasons ?: DataSeasons()
        dataEpisode = intent.getSerializableExtra("episode") as? DataEpisodes ?: DataEpisodes()

        seasonPosition = intent.getIntExtra("season_position", 0)
        episodePosition = intent.getIntExtra("episode_position", 1)

        binding.imagePoster.setBackgroundResource(R.drawable.loading_animation)
        val image = binding.imagePoster.background as AnimationDrawable
        image.start()

        thread {
            initDataVideo()
        }

        setContentView(binding.root)
    }

    private fun initDataVideo()
    {
        // mendapatkan data season (jumlah season untuk jumlah episode dan directory)
        if (dataSeasons.season_id == -1)
        {
            val result = JSONEncodeParser.retrieveJSONArrayFromNetwork("${getString(R.string.server)}/PVTS/video_finder.php?season_from_movie_id=${dataMovie.id}")

            if (result.size == 1)
            {
                val data = result[0]
                if (data is List<*> && data.size == 6)
                {
                    dataSeasons.changeData(
                        (data[0] as String).toInt(),
                        data[3] as String,
                        data[5] as String,
                        (data[4] as String).toInt()
                    )
                }
                else
                {
                    throw UnsupportedOperationException()
                }
            }
            else
            {
                throw UnsupportedOperationException()
            }
        }

        thread {
            loadPoster()
        }
        thread {
            loadVideo()
        }
    }

    private fun loadVideo() {
        if (dataEpisode.id == -1)
        {

            val result = JSONEncodeParser.retrieveJSONArrayFromNetwork("${getString(R.string.server)}/PVTS/video_finder.php?season_id=${dataSeasons.season_id}")

            if (result.size == 1)
            {
                val videoData = result[0]
                if (videoData is List<*> && videoData.size == 3)
                {
                    dataEpisode.changeData(
                        (videoData[0] as String).toInt(),
                        videoData[1] as String,
                        videoData[2] as String)
                }
                else
                {
                    throw UnsupportedOperationException()
                }
            }
            else
            {
                throw UnsupportedOperationException()
            }
        }

        runOnUiThread {
            if (dataMovie.seasonAmount >= 2) {
                binding.videoPlayer.setVideoPath("${getString(R.string.server)}/${dataMovie.url}/${dataSeasons.directory}/${dataEpisode.fileName}")
            } else {
                binding.videoPlayer.setVideoPath("${getString(R.string.server)}/${dataMovie.url}/${dataEpisode.fileName}")
            }
            binding.videoPlayer.start()
        }


    }

    private fun loadPoster()
    {
        runOnUiThread {
            binding.movieTitle.text = dataMovie.title
            if (dataMovie.seasonAmount == 0)
            {
                binding.season.text = "Season: ${dataSeasons.title}"
            }
            else
            {
                binding.season.text = "Season: $seasonPosition/${dataMovie.seasonAmount}"
            }
            binding.episode.text = "Episode: $episodePosition/${dataSeasons.totalEpisode}"
        }
        while(true)
        {
            try {
                val connection = URL("${getString(R.string.server)}/${dataMovie.url}/poster")
                val bitmap = BitmapFactory.decodeStream(connection.openConnection().getInputStream())

                runOnUiThread {
                    (binding.imagePoster.background as AnimationDrawable).stop()
                    binding.imagePoster.setImageBitmap(bitmap)
                }
                break
            }
            catch (e : IOException)
            {
                if (e is NoSuchFileException)
                {
                    break
                }
            }
        }
    }
}