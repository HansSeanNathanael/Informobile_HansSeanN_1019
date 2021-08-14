package com.tainka.pvts.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tainka.pvts.R
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.databinding.ActivityMainMenuBinding
import com.tainka.pvts.utilities.DummyCardViewTest
import com.tainka.pvts.utilities.JSONEncodeParser
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class MainMenuActivity : AppCompatActivity() {

    private var accountName : String? = null

    private var _activityBinding : ActivityMainMenuBinding? = null
    private val binding get() = _activityBinding!!

    private lateinit var homeMovieCardAdapter : HomeViewAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _activityBinding = ActivityMainMenuBinding.inflate(layoutInflater)

        accountName = intent.getStringExtra("AccountName")
        setAccountName(accountName)



        binding.buttonOpenSetting.setOnClickListener()
        {
            val settingMenu = binding.settingMenu
            settingMenu.visibility = View.VISIBLE
        }

        binding.buttonCloseSetting.setOnClickListener()
        {
            val settingMenu = binding.settingMenu
            settingMenu.visibility = View.GONE
        }

        binding.buttonCloseSettingOut.setOnClickListener()
        {
            val settingMenu = binding.settingMenu
            settingMenu.visibility = View.GONE
        }

        binding.buttonLogOut.setOnClickListener()
        {
            val intent = Intent(this@MainMenuActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        homeMovieCardAdapter = HomeViewAdapter(this)

        binding.listCardView.apply {
            layoutManager = LinearLayoutManager(this@MainMenuActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = homeMovieCardAdapter
        }

        homeMovieCardAdapter.setList(DummyCardViewTest.GetMovieCard())

        thread {
            val url = "http://192.168.100.8/PVTS/home_video_poster.php?retrieve=1"

            val homeMovieCardDataMovie = getHomeMovieCard(url)

            this@MainMenuActivity.runOnUiThread {
                homeMovieCardAdapter.setList(homeMovieCardDataMovie)
            }
        }

        setContentView(binding.root)
    }


    private fun setAccountName(name : String?)
    {
        if (name == null)
        {
            throw IllegalArgumentException("Account Name Error")
        }

        val textAccountName = binding.textViewAccountName
        textAccountName.text = name
    }



    fun getHomeMovieCard(url: String) : List<DataMovie>
    {
        var homeMovieCardDataMovie : MutableList<DataMovie> = mutableListOf()

        var jsonArrayFromNetwork = JSONEncodeParser.retrieveJSONArrayFromNetwork(url)

        for (i in jsonArrayFromNetwork)
        {
            if (i is List<*> && i.size == 4)
            {
                if (i[0] is String && i[1] is String && i[2] is String && i[3] is String)
                {
                    homeMovieCardDataMovie.add(
                        DataMovie(
                            (i[0] as? String ?: "-1").toInt(),
                            i[2] as? String ?: "error",
                            (i[1] as? String ?: "-1").toInt(),
                            i[3] as? String ?: "error"
                        )
                    )
                }
            }
        }

        return homeMovieCardDataMovie
    }

    fun processPage(movie : DataMovie)
    {
        if (movie.seasonAmount == 0)
        {
            val intent = Intent(this@MainMenuActivity, VideoPageActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
        else if (movie.seasonAmount >= 2)
        {
            val intent = Intent(this@MainMenuActivity, SeasonPageActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
        else
        {
            val intent = Intent(this@MainMenuActivity, EpisodePageActivity::class.java)
            intent.putExtra("movie", movie)
            intent.putExtra("season_position", 1)
            startActivity(intent)
        }
    }
}