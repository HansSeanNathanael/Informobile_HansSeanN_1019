package com.tainka.pvts.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tainka.pvts.R
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.data.DataSeasons
import com.tainka.pvts.databinding.ActivitySeasonPageBinding
import com.tainka.pvts.utilities.JSONEncodeParser
import com.tainka.pvts.utilities.LoadingCard
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class SeasonPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySeasonPageBinding

    private lateinit var seasonCardAdapter: SeasonViewAdapter
    private lateinit var movie : DataMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        movie = (intent.getSerializableExtra("movie") as? DataMovie ?: DataMovie())!!
        if (movie.id == -1)
        {
            throw UnsupportedOperationException()
        }

        binding = ActivitySeasonPageBinding.inflate(layoutInflater)

        binding.movieTitle.text = movie.title
        binding.totalSeason.text = "Total season: ${movie.seasonAmount}"

        binding.moviePoster.setBackgroundResource(R.drawable.loading_animation)
        var image = binding.moviePoster.background as AnimationDrawable
        image.start()

        thread {
            processPoster()
        }

        seasonCardAdapter = SeasonViewAdapter(this)

        binding.seasonCardList.apply {
            layoutManager = LinearLayoutManager(this@SeasonPageActivity, LinearLayoutManager.VERTICAL, false)
            adapter = seasonCardAdapter
        }

        seasonCardAdapter.setListItem(LoadingCard.SeasonCard.getSeasonCard(movie.seasonAmount))

        thread {
            var listSeasons = getSeasonData()
            runOnUiThread {
                seasonCardAdapter.setListItem(listSeasons)
            }
        }

        setContentView(binding.root)
    }

    private fun getSeasonData() : List<DataSeasons>
    {
        val returnValue : MutableList<DataSeasons> = mutableListOf()

        val result = JSONEncodeParser.retrieveJSONArrayFromNetwork("${getString(R.string.server)}/PVTS/video_finder.php?season_from_movie_id=${movie.id}")

        for (i in result)
        {
            with(i as List<String>)
            {
                returnValue.add(
                    DataSeasons(
                        i[0].toInt(), i[3], i[5], i[4].toInt()
                    )
                )
            }
        }
        return returnValue
    }

    private fun processPoster()
    {
        while(true)
        {
            try
            {
                val connection = URL("${getString(R.string.server)}/${movie.url}/poster")
                val bitmap = BitmapFactory.decodeStream(connection.openConnection().getInputStream())

                runOnUiThread {
                    (binding.moviePoster.background as AnimationDrawable).stop()
                    binding.moviePoster.setImageBitmap(bitmap)
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

    fun processPage(seasons: DataSeasons, position : Int)
    {
        if (seasons.totalEpisode == 1)
        {
            val intent = Intent(this@SeasonPageActivity, VideoPageActivity::class.java)
            intent.putExtra("movie", movie)
            intent.putExtra("season", seasons)
            intent.putExtra("season_position", position)

            startActivity(intent)
        }
        else
        {
            val intent = Intent(this@SeasonPageActivity, EpisodePageActivity::class.java)
            intent.putExtra("movie", movie)
            intent.putExtra("season", seasons)
            intent.putExtra("season_position", position)

            startActivity(intent)
        }
    }
}