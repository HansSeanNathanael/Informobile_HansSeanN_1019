package com.tainka.pvts.utilities

import com.tainka.pvts.data.DataMovie

object DummyCardViewTest {
    fun GetMovieCard() : List<DataMovie>
    {
        var returnValue : ArrayList<DataMovie> = arrayListOf()

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