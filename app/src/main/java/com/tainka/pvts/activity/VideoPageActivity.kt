package com.tainka.pvts.activity

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.RelativeLayout
import com.tainka.pvts.R
import com.tainka.pvts.data.DataEpisodes
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.data.DataSeasons
import com.tainka.pvts.databinding.ActivityVideoPageBinding
import com.tainka.pvts.utilities.*
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class VideoPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVideoPageBinding

    var handler = Handler(Looper.getMainLooper())

    private var seasonPosition = 0
    private var episodePosition = 0

    private lateinit var dataSeasons : DataSeasons
    private lateinit var dataMovie : DataMovie
    private lateinit var dataEpisode : DataEpisodes

    lateinit var timeBinderControllPlayer : TimeBinderControllPlayer

    private var fullScreenVideoPlayer = false

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

        timeBinderControllPlayer = TimeBinderControllPlayer(this, binding)
        val seekBarChangeListener = SeekBarChangeListener(this, binding)

        fullScreenVideoPlayer = false

        binding.videoPlayer.setOnPreparedListener {
            binding.playerController.buttonPlay.setImageResource(R.drawable.ic_pause)

            val duration = binding.videoPlayer.duration
            var hourColoumn = false
            if (duration / 3600000 > 0)
            {
                hourColoumn = true
            }
            val durationText = getDuration(duration)

            binding.playerController.time.text = "${seekBarChangeListener.getCurrentTimePosition(hourColoumn, 0)}/$durationText"
            binding.playerController.seekbar.max = duration

            seekBarChangeListener.initTime(hourColoumn, durationText)

            binding.playerController.seekbar.setOnSeekBarChangeListener(seekBarChangeListener)

            binding.playerController.buttonPlay.setOnClickListener {
                playButtonPressed()
            }

            handler.postDelayed(timeBinderControllPlayer, 100)
        }

        binding.videoPlayer.setOnCompletionListener {
            binding.playerController.buttonPlay.setOnClickListener(null)
        }

        binding.playerController.buttonFullscreen.setOnClickListener {
            toggleFullscreen()
        }


        val touchVideoPlayerListener = TouchVideoPlayerListener(this, binding, 5000, 500)

        binding.videoPlayer.setOnTouchListener(touchVideoPlayerListener)

        setContentView(binding.root)
    }

    override fun onDestroy() {
        handler.removeCallbacks(timeBinderControllPlayer)
        binding.videoPlayer.stopPlayback()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        configureVideoPlayerSize()
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

    private fun getDuration(time : Int) : String
    {
        val hour = time / 3600000
        val minute = (time % 3600000) / 60000
        val second = (time % 60000) / 1000
        if (hour > 0)
        {
            return String.format("%02d:%02d:%02d", hour, minute, second)
        }
        else
        {
            return String.format("%02d:%02d", minute, second)
        }
    }

    private fun playButtonPressed()
    {
        if (binding.videoPlayer.isPlaying)
        {
            pauseVideo()
        }
        else
        {
            resumeVideo()
        }
    }

    fun pauseVideo()
    {
        binding.videoPlayer.pause()
        binding.playerController.buttonPlay.setImageResource(R.drawable.ic_play_arrow)
    }

    fun resumeVideo()
    {
        binding.videoPlayer.start()
        binding.playerController.buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    fun toggleFullscreen()
    {
        if (fullScreenVideoPlayer)
        {
            exitFullScreenMode()
        }
        else
        {
            enterFullScreenMode()
        }
        fullScreenVideoPlayer = !fullScreenVideoPlayer

    }

    private fun enterFullScreenMode()
    {
        binding.playerController.buttonFullscreen.setImageResource(R.drawable.ic_fullscreen_exit)

        val videoRectBoundParam = binding.videoRectBound.layoutParams as RelativeLayout.LayoutParams
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        binding.videoRectBound.layoutParams = videoRectBoundParam
    }

    private fun exitFullScreenMode()
    {
        val videoRectBoundParam = binding.videoRectBound.layoutParams as RelativeLayout.LayoutParams
        videoRectBoundParam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        videoRectBoundParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        binding.videoRectBound.layoutParams = videoRectBoundParam


        binding.playerController.buttonFullscreen.setImageResource(R.drawable.ic_fullscreen_enter)
    }

    private fun configureVideoPlayerSize()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())

            val displayMetrics = windowManager.currentWindowMetrics
            val width = displayMetrics.bounds.width()
            val height = displayMetrics.bounds.height()

            val videoPlayerParam = binding.videoPlayer.layoutParams

            if (width / 16 <= height / 9)
            {
                videoPlayerParam.width = WindowManager.LayoutParams.MATCH_PARENT
                videoPlayerParam.height = width / 16 * 9
            }
            else
            {
                videoPlayerParam.width = height / 9 * 16
                videoPlayerParam.height = WindowManager.LayoutParams.MATCH_PARENT
            }

            binding.videoPlayer.layoutParams = videoPlayerParam

        }
        else
        {
            val displayMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                window.insetsController?.hide(WindowInsets.Type.statusBars())

                val display = this.display

                @Suppress("DEPRECATION")
                display?.getRealMetrics(displayMetrics)
            }
            else
            {
                @Suppress("DEPRECATION")
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getMetrics(displayMetrics)
            }


            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            val videoPlayerParam = binding.videoPlayer.layoutParams
            val smallestPart : Int
            if (width / 16 < height / 9)
            {
                smallestPart = width / 16
            }
            else
            {
                smallestPart = height / 9
            }
            videoPlayerParam.height = smallestPart * 9
            videoPlayerParam.width = smallestPart * 16

            binding.videoPlayer.layoutParams = videoPlayerParam
        }
    }
}