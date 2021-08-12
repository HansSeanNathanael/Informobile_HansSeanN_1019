package com.tainka.pvts.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.tainka.pvts.data.Seasons
import com.tainka.pvts.databinding.ActivityVideoPageBinding
import com.tainka.pvts.utilities.JSONEncodeParser
import kotlin.concurrent.thread

class VideoPageActivity : AppCompatActivity() {

    var movieList : MutableList<Seasons> = mutableListOf()
    lateinit var binding : ActivityVideoPageBinding
    var videoURL = ""
    var seasonID = 0
    var fileName = ""
    var movieID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPageBinding.inflate(layoutInflater)

        videoURL = intent.getStringExtra("url") ?: "null"
        fileName = intent.getStringExtra("filename") ?: "GET FROM DATABASE"
        seasonID = (intent.getStringExtra("season_id") ?: "0").toInt()
        movieID = (intent.getStringExtra("movie_id") ?: "0").toInt()

        loadVideo(videoURL, fileName)

        setContentView(binding.root)
    }

    private fun loadVideo(url : String, filename : String = "GET FROM DATABASE")
    {
        if (filename == "GET FROM DATABASE")
        {
            thread {
                if (seasonID == 0)
                {
                    var result = JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?season_from_movie_id=${movieID.toString()}")
                    if (result.size == 1)
                    {
                        var seasonData = result[0]
                        if (seasonData is List<*> && seasonData.size == 6)
                        {
                            seasonID = (seasonData[0] as String).toInt()
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

                    result = JSONEncodeParser.retrieveJSONArrayFromNetwork("http://192.168.100.8/PVTS/video_finder.php?season_id=${seasonID.toString()}")
                    if (result.size == 1)
                    {
                        var seasonData = result[0]
                        if (seasonData is List<*> && seasonData.size == 3)
                        {
                            fileName = seasonData[2] as String
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
                else
                {
                    throw UnsupportedOperationException()
                }

                runOnUiThread {
                    binding.videoPlayer.setVideoPath("http://192.168.100.8/$url/$fileName")
                    binding.videoPlayer.start()
                }
            }
        }
        else
        {
            binding.videoPlayer.setVideoPath("$url/$filename")
            binding.videoPlayer.start()
        }
    }
}