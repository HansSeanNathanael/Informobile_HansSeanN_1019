package com.tainka.pvts.activity

import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.tainka.pvts.R
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

    private var videoID = 0
    private var episodePosition = 0

    private var fileName = ""

    private lateinit var dataSeasons : DataSeasons
    private lateinit var dataMovie : DataMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPageBinding.inflate(layoutInflater)

        dataMovie = intent.getSerializableExtra("movie") as DataMovie
        dataSeasons = (intent.getSerializableExtra("season") as? DataSeasons ?: DataSeasons())!!

        seasonPosition = intent.getIntExtra("season", 0)
        episodePosition = intent.getIntExtra("episode_position", 1)
        videoID = intent.getIntExtra("video_id", 0)

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
        {/*
            //bila id season ada
            var result = JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?get_season_data_id=${seasonID}")

            if (result[0] is List<*>)
            {
                var data = result[0] as List<*>

                if (data.size == 5)
                {
                    seasonData = DataSeasons(seasonID, data[2] as String, data[4] as String, (data[3] as String).toInt())
                }
                else
                {
                    throw java.lang.UnsupportedOperationException()
                }
            }
            else
            {
                throw java.lang.UnsupportedOperationException()
            }
        }
        else
        {*/

            // bila hanya diberi movie saja (untuk yang hanya 1 season atau movie)
            var result = JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?season_from_movie_id=${dataMovie.id}")

            if (result.size == 1)
            {
                var data = result[0]
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
            loadVideo(videoID)
        }
    }

    private fun loadVideo(id_video : Int) {
        thread {
            if (id_video == 0) {


                var result =
                    JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?season_id=${dataSeasons.season_id}")
                if (result.size == 1) {
                    var videoData = result[0]
                    if (videoData is List<*> && videoData.size == 3) {
                        videoID = (videoData[0] as String).toInt()
                        fileName = videoData[2] as String
                    } else {
                        throw UnsupportedOperationException()
                    }
                } else {
                    throw UnsupportedOperationException()
                }

                runOnUiThread {
                    if (dataMovie.seasonAmount >= 2) {
                        binding.videoPlayer.setVideoPath("http://192.168.100.8/${dataMovie.url}/${dataSeasons.directory}/$fileName")
                    } else {
                        binding.videoPlayer.setVideoPath("http://192.168.100.8/${dataMovie.url}/$fileName")
                    }
                    binding.videoPlayer.start()
                }
            } else {
                var result =
                    JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?video_id=${id_video}")

                if (result.size == 1) {
                    var videoData = result[0] as List<String>
                    fileName = videoData[1]

                    runOnUiThread {
                        if (dataMovie.seasonAmount >= 2) {
                            binding.videoPlayer.setVideoPath("http://192.168.100.8/${dataMovie.url}/${dataSeasons.directory}/$fileName")
                        } else {
                            binding.videoPlayer.setVideoPath("http://192.168.100.8/${dataMovie.url}/$fileName")
                        }
                        binding.videoPlayer.start()
                    }
                }
            }
        }
    }

    private fun loadPoster()
    {
        runOnUiThread {
            binding.movieTitle.text = dataMovie.title
            if (dataMovie.seasonAmount == 0)
            {
                binding.season.text = "Season: MOVIE"
            }
            binding.episode.text = "Episode: $episodePosition/${dataSeasons.totalEpisode}"
        }
        while(true)
        {
            try {
                val connection = URL("http://192.168.100.8/${dataMovie.url}/poster")
                var bitmap = BitmapFactory.decodeStream(connection.openConnection().getInputStream())

                runOnUiThread {
                    (binding.imagePoster.background as AnimationDrawable).stop()
                    binding.imagePoster.setImageBitmap(bitmap)
                }
                break
            }
            catch (e : IOException)
            {

            }
        }
    }
}