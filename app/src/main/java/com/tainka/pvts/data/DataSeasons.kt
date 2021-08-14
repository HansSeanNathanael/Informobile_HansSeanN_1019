package com.tainka.pvts.data

import java.io.Serializable

data class DataSeasons (var season_id : Int = -1, var title : String = "Loading", var directory : String = "error", var totalEpisode : Int = 0) : Serializable
{
    fun changeData(id : Int, newTitle : String, newDirectory : String, newTotalEpisode : Int)
    {
        this.season_id = id
        this.title = newTitle
        this.directory = newDirectory
        this.totalEpisode = newTotalEpisode
    }
}