package com.tainka.pvts.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.tainka.pvts.R
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.databinding.ActivityVideoCardListBinding
import com.tainka.pvts.utilities.JSONEncodeParser
import com.tainka.pvts.utilities.LoadingCard
import kotlin.concurrent.thread

class VideoCardListActivity : AppCompatActivity() {

    lateinit var binding : ActivityVideoCardListBinding
    var URL = ""
    var title = ""
    var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoCardListBinding.inflate(layoutInflater)

        URL = intent.getStringExtra("URL") ?: ""
        title = intent.getStringExtra("title") ?: ""

        binding.listInfoText.text = title


        val cardAdapter = CardListAdapter(this)

        binding.listCard.apply {
            layoutManager = GridLayoutManager(this@VideoCardListActivity, 2, GridLayoutManager.VERTICAL, false)
            adapter = cardAdapter
        }

        thread {
            val totalMovie = getTotalMovie()
            runOnUiThread {
                if (totalMovie <= 25)
                {
                    cardAdapter.setList(LoadingCard.MovieCard.getMovieCard(totalMovie))
                }
                else
                {
                    cardAdapter.setList(LoadingCard.MovieCard.getMovieCard(25))
                }
            }

            val cardList = getMovieCardPage(URL, 1)
            runOnUiThread {
                cardAdapter.setList(cardList)
            }
        }

        setContentView(binding.root)
    }

    private fun getTotalMovie() : Int
    {
        val url = "${getString(R.string.server)}/PVTS/home_video_poster.php?total_movie=1"
        var result = JSONEncodeParser.retrieveJSONArrayFromNetwork(url)
        return (result[0] as String).toInt()
    }

    private fun getMovieCardPage(url : String, page : Int) : List<DataMovie>
    {
        val returnList : MutableList<DataMovie> = mutableListOf()
        val result = JSONEncodeParser.retrieveJSONArrayFromNetwork("$url$page")

        for (i in result)
        {
            if (i is List<*>)
            {
                if (i.size == 4)
                {
                    with(i as List<String>)
                    {
                        returnList.add(
                            DataMovie(
                                i[0].toInt(), i[2], i[1].toInt(), i[3]
                            )
                        )
                    }
                }
            }
        }
        return returnList
    }

    fun processPage(movie : DataMovie)
    {
        if (movie.seasonAmount == 0)
        {
            val intent = Intent(this@VideoCardListActivity, VideoPageActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
        else if (movie.seasonAmount >= 2)
        {
            val intent = Intent(this@VideoCardListActivity, SeasonPageActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
        else
        {
            val intent = Intent(this@VideoCardListActivity, EpisodePageActivity::class.java)
            intent.putExtra("movie", movie)
            intent.putExtra("season_position", 1)
            startActivity(intent)
        }
    }
}