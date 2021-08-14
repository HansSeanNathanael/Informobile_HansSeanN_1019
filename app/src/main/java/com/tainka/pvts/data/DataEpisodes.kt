package com.tainka.pvts.data

import java.io.Serializable

data class DataEpisodes(var id : Int = -1, var title : String = "", var fileName : String = "") : Serializable
{
    fun changeData(newID : Int, newTitle : String, newFileName : String)
    {
        id = newID
        title = newTitle
        fileName = newFileName
    }
}
