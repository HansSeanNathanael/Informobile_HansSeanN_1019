package com.tainka.pvts.utilities

import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.tainka.pvts.activity.VideoPageActivity
import com.tainka.pvts.databinding.ActivityVideoPageBinding

class SeekBarChangeListener(var videoPageActivity : VideoPageActivity, var binding : ActivityVideoPageBinding) : SeekBar.OnSeekBarChangeListener {

    var hourColoumn: Boolean = false
    var durationText : String = ""

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser)
        {
            binding.videoPlayer.pause()
            binding.videoPlayer.seekTo(progress)
            binding.videoPlayer.start()
            seekBar?.progress = progress

        }
        val time = binding.videoPlayer.currentPosition
        binding.playerController.time.text = "${getCurrentTimePosition(hourColoumn, time)}/$durationText"

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        videoPageActivity.pauseVideo()
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        videoPageActivity.resumeVideo()
    }

    fun initTime(hourColoumn: Boolean, durationText : String)
    {
        this.hourColoumn = hourColoumn
        this.durationText = durationText
    }

    fun getCurrentTimePosition(hourColoumn : Boolean, time : Int) : String
    {
        val hour = time / 3600000
        val minute = (time % 3600000) / 60000
        val second = (time % 60000) / 1000
        if (hourColoumn)
        {
            return String.format("%02d:%02d:%02d", hour, minute, second)
        }
        else
        {
            return String.format("%02d:%02d", minute, second)
        }
    }
}

class TouchVideoPlayerListener(var videoPageActivity: VideoPageActivity, var binding: ActivityVideoPageBinding, hideTime : Long, forwardTime : Long) : View.OnTouchListener {

    var forward = false
    var firstClick = false

    private var hideControllTimer = object  : CountDownTimer(hideTime, 1000) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            binding.playerController.root.visibility = View.INVISIBLE
        }
    }

    private var forwardTimer = object  : CountDownTimer(forwardTime, 500) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {

            if (binding.playerController.root.visibility == View.INVISIBLE && !forward)
            {
                binding.playerController.root.visibility = View.VISIBLE
                hideControllTimer.start()
            }
            else if (binding.playerController.root.visibility == View.VISIBLE)
            {
                if (forward)
                {
                    hideControllTimer.start()
                }
                else
                {
                    binding.playerController.root.visibility = View.INVISIBLE
                    hideControllTimer.cancel()
                }
            }


            firstClick = false
            forward = false

            binding.videoPlayer.start()
        }
    }

    init {
        hideControllTimer.start()
    }

    override fun onTouch(view: View?, event : MotionEvent?): Boolean {


        if (event?.action == MotionEvent.ACTION_DOWN)
        {
            if (firstClick)
            {
                forward = true
                forwardTimer.cancel()
                forwardTimer.start()
                binding.videoPlayer.pause()

                val xSize = binding.videoPlayer.layoutParams.width
                val position = event.x / xSize.toFloat()
                if (position > 0 && position <= 0.5)
                {
                    binding.videoPlayer.seekTo(binding.videoPlayer.currentPosition - 5000)
                }
                else if (position > 0.5 && position <= 1)
                {
                    binding.videoPlayer.seekTo(binding.videoPlayer.currentPosition + 5000)
                }
                hideControllTimer.cancel()
            }
            else
            {
                forwardTimer.start()
                firstClick = true
            }
        }

        return true
    }

}

class TimeBinderControllPlayer(var videoPageActivity: VideoPageActivity, var binding : ActivityVideoPageBinding) : Runnable
{

    override fun run()
    {
        val timePosition = binding.videoPlayer.currentPosition
        val bufferPercentage = binding.videoPlayer.bufferPercentage
        videoPageActivity.runOnUiThread {
            binding.playerController.seekbar.progress = timePosition
            binding.playerController.seekbar.secondaryProgress = binding.playerController.seekbar.max * bufferPercentage / 100

            videoPageActivity.handler.postDelayed(this, 100)
        }
    }
}