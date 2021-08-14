package com.tainka.pvts.utilities

import com.tainka.pvts.data.DataEpisodes
import com.tainka.pvts.data.DataMovie
import com.tainka.pvts.data.DataSeasons

object LoadingCard {
    object SeasonCard {
        fun getSeasonCard(elements : Int) : List<DataSeasons>
        {
            val returnValue = ArrayList<DataSeasons>()

            for (i in 0 until elements)
            {
                returnValue.add(DataSeasons())
            }

            return returnValue
        }
    }

    object MovieCard {
        fun getMovieCard() : List<DataMovie>
        {
            val returnValue : ArrayList<DataMovie> = arrayListOf()

            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))
            returnValue.add(DataMovie(-1, "Loading", -1, ""))

            return returnValue
        }
    }

    object EpisodeCard {
        fun getEpisodeCard(elements : Int) : List<DataEpisodes>
        {
            val returnValue = ArrayList<DataEpisodes>()

            for (i in 0 until elements)
            {
                returnValue.add(DataEpisodes())
            }

            return returnValue
        }
    }
}