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

        homeMovieCardAdapter = HomeViewAdapter()

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

    private fun getNetworkText(url : String) : String
    {
        var returnValue : String = ""
        val connection : URL = URL(url)

        while(true)
        {
            try
            {
                val text = connection.readText()
                var firstIndex = text.indexOf("<body>", 0,true)
                while(text[firstIndex] != '"' && text[firstIndex] != '[' && firstIndex < text.length)
                {
                    firstIndex += 1
                }
                var lastIndex = text.indexOf("</body>", firstIndex, true)
                while(text[lastIndex] != '"' && text[lastIndex] != ']' && lastIndex > 0)
                {
                    lastIndex -= 1
                }
                returnValue = text.substring(firstIndex, lastIndex+1)
                break
            }
            catch (e : IOException)
            {

            }
        }
        return returnValue
    }

    private fun retrieveJSONArrayFromNetwork(url : String) : List<Any>
    {
        var parsedList : MutableList<Any> = mutableListOf()
        var jsonText : String = ""

        while(true)
        {
            try
            {
                jsonText = getNetworkText(url)
                break
            }
            catch (e :IOException)
            {
                continue
            }
        }

        if (jsonText[0] != '[')
        {
            for (i in jsonText.indices)
            {
                if (jsonText[i] == '"')
                {
                    var secondMarkLocation = jsonText.indexOf('"', i+1, false)
                    parsedList.add(jsonText.substring(i+1, secondMarkLocation))
                }
            }
        }
        else
        {
            parsedList.add(jsonText[0])

            var i = 1
            while (i < jsonText.length-1)
            {
                if (jsonText[i] == '[')
                {
                    parsedList.add(jsonText[i])
                }
                else if (jsonText[i] == ']')
                {
                    val newElement = ArrayList<Any>()
                    while (parsedList.isNotEmpty())
                    {
                        if (parsedList.last() is Char && parsedList.last() == '[')
                        {
                            parsedList.removeLast()
                            break
                        }
                        else
                        {
                            newElement.add(parsedList.last())
                            parsedList.removeLast()
                        }
                    }
                    parsedList.add(newElement)
                }
                else if (jsonText[i] == '"')
                {
                    val secondMarkLocation = jsonText.indexOf('"', i + 1, false)
                    parsedList.add(jsonText.substring(i + 1, secondMarkLocation))
                    i = secondMarkLocation
                }

                i++
            }
        }

        return parsedList
    }

    fun getHomeMovieCard(url: String) : List<DataMovie>
    {
        var homeMovieCardDataMovie : MutableList<DataMovie> = mutableListOf()

        var jsonArrayFromNetwork = retrieveJSONArrayFromNetwork(url)

        for (i in jsonArrayFromNetwork)
        {
            if (i is List<*> && i.size == 4)
            {
                if (i[0] is String && i[1] is String && i[2] is String && i[3] is String)
                {
                    homeMovieCardDataMovie.add(
                        DataMovie(
                            i[3] as? Int ?: -1,
                            i[1] as? String ?: "error",
                            i[2] as? Int ?: -1,
                            i[0] as? String ?: "error"
                        )
                    )
                }
            }
        }

        return homeMovieCardDataMovie
    }
}