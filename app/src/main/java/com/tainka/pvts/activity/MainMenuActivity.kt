package com.tainka.pvts.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tainka.pvts.R
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.databinding.ActivityMainMenuBinding
import com.tainka.pvts.utilities.JSONEncodeParser
import com.tainka.pvts.utilities.LoadingCard
import java.util.*
import kotlin.concurrent.thread

class MainMenuActivity : AppCompatActivity() {

    private var accountName : String? = null

    private var _activityBinding : ActivityMainMenuBinding? = null
    private val binding get() = _activityBinding!!

    private lateinit var homeMovieCardAdapterRecentlyAdded : HomeViewAdapter
    private lateinit var homeMovieCardAdapterRecentlyUpdated : HomeViewAdapter

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

        homeMovieCardAdapterRecentlyAdded = HomeViewAdapter(this)
        homeMovieCardAdapterRecentlyUpdated = HomeViewAdapter(this)

        binding.listCardView.apply {
            layoutManager = LinearLayoutManager(this@MainMenuActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = homeMovieCardAdapterRecentlyAdded
        }

        binding.listCardView2.apply {
            layoutManager = LinearLayoutManager(this@MainMenuActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = homeMovieCardAdapterRecentlyUpdated
        }

        homeMovieCardAdapterRecentlyAdded.setList(LoadingCard.MovieCard.getMovieCard())
        homeMovieCardAdapterRecentlyUpdated.setList(LoadingCard.MovieCard.getMovieCard())

        thread { // listCard Recently Added
            val url = "${getString(R.string.server)}/PVTS/home_video_poster.php?retrieve=1"

            val homeMovieCardDataMovie = getHomeMovieCard(url)

            this@MainMenuActivity.runOnUiThread {
                homeMovieCardAdapterRecentlyAdded.setList(homeMovieCardDataMovie)
            }
        }

        binding.buttonMoreRecentlyAdded.setOnClickListener {
            val intent = Intent(this@MainMenuActivity, VideoCardListActivity::class.java)
            intent.putExtra("URL", "${getString(R.string.server)}/PVTS/home_video_poster.php?retrieve=2&page=")
            intent.putExtra("title", resources.getString(R.string.recently_added))
            startActivity(intent)
        }

        thread { // list Card recently Updated
            val url = "${getString(R.string.server)}/PVTS/home_video_poster.php?retrieve=3"

            val homeMovieCardDataMovie = getHomeMovieCard(url)

            this@MainMenuActivity.runOnUiThread {
                homeMovieCardAdapterRecentlyUpdated.setList(homeMovieCardDataMovie)
            }
        }

        binding.buttonMoreRecentlyUpdated.setOnClickListener {
            val intent = Intent(this@MainMenuActivity, VideoCardListActivity::class.java)
            intent.putExtra("URL", "${getString(R.string.server)}/PVTS/home_video_poster.php?retrieve=4&page=")
            intent.putExtra("title", resources.getString(R.string.recently_updated))
            startActivity(intent)
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
        val homeMovieCardDataMovie : MutableList<DataMovie> = mutableListOf()

        val jsonArrayFromNetwork = JSONEncodeParser.retrieveJSONArrayFromNetwork(url)

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