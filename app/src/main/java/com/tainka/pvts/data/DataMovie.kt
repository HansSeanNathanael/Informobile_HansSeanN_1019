package com.tainka.pvts.data

import java.io.Serializable

data class DataMovie(var id : Int = -1, var title : String = "ERROR", var seasonAmount : Int = -1, var url : String = "ERROR") : Serializable
{
    constructor(list : List<String>) : this(list[0].toInt(), list[2], list[1].toInt(), list[3])
}