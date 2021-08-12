package com.tainka.pvts.utilities

import java.io.IOException
import java.net.URL

object JSONEncodeParser {
    fun getNetworkText(url : String) : String
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

    fun retrieveJSONArrayFromNetwork(url : String) : List<Any>
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
            catch (e : IOException)
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
            var i = 1
            while (i < jsonText.length-1)
            {
                if (jsonText[i] == '[')
                {
                    parsedList.add(jsonText[i])
                }
                else if (jsonText[i] == ']')
                {
                    val newElement : MutableList<Any> = mutableListOf()
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
                    newElement.reverse()
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
}