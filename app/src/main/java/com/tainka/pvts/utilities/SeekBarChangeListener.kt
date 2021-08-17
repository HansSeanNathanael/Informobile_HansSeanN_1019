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

class TouchVideoPlayerListener(var videoPageActivity: VideoPageActivity, var binding: ActivityVideoPageBinding, var hideControllTimer: CountDownTimer) : View.OnTouchListener {
    override fun onTouch(view: View?, event : MotionEvent?): Boolean {

        if (event?.action == MotionEvent.ACTION_DOWN)
        {
            if (binding.playerController.root.visibility == View.INVISIBLE)
            {
                binding.playerController.root.visibility = View.VISIBLE
                hideControllTimer.start()
            }
            else
            {
                binding.playerController.root.visibility = View.INVISIBLE
                hideControllTimer.cancel()
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
        videoPageActivity.runOnUiThread {
            Log.d("Tes", timePosition.toString())
            binding.playerController.seekbar.progress = timePosition

            videoPageActivity.handler.postDelayed(this, 100)
        }
    }
}