package com.example.ituneplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

class iTuneXMLParser(val listener: ParseListener) {
    private val factory = XmlPullParserFactory.newInstance()
    private val parser = factory.newPullParser()
    private val data = mutableListOf<SongData>()
    private var songTitle = ""
    private var text = ""
    private var imageFound = false
    private var songCover: Bitmap? = null
    private var songUrl: String = ""

    fun parseURL(url : String){
        listener.start()
        GlobalScope.launch {
            try{
                val inputStream = URL(url).openStream()
                parser.setInput(inputStream, null)
                // retrieve the next XML tag
                var eventType = parser.next()

                // when we are not reach the XML end just iterate
                while(eventType != XmlPullParser.END_DOCUMENT){
                    // retrieve the name of the tag
                    var tagName = parser.name
                    if(tagName.equals("entry",ignoreCase = true) && eventType == XmlPullParser.START_TAG){
                        // extract everything inside the entry tag until the end of the entry tag and ignore everything else
                        while (!(tagName.equals("entry",ignoreCase = true) && eventType == XmlPullParser.END_TAG)) {
                            when(eventType) {
                                XmlPullParser.START_TAG -> if(tagName.equals("media:thumbnail",ignoreCase = true)){
                                    imageFound = parser.getAttributeValue(null, "url") != null
                                } else if (tagName.equals("link",false)){
//                                    if(parser.getAttributeValue(null,"type").equals("audio/x-m4a")){
                                        songUrl = parser.getAttributeValue(null,"href")
//                                    }
                                }
                                XmlPullParser.TEXT -> text = parser.text
                                XmlPullParser.END_TAG -> if(tagName.equals("title",ignoreCase = true)){
                                    songTitle = text
                                }
                                else if(tagName.equals("media:thumbnail", ignoreCase = true) && imageFound) {
                                    val url = parser.getAttributeValue(null, "url")
                                    Log.i("URL", url)
                                    val inputStream = URL(url).openStream()
                                    songCover = BitmapFactory.decodeStream(inputStream)
                                    imageFound = false
                                }
                            }
                            eventType = parser.next()
                            tagName = parser.name
                        }

                        data.add(SongData(songTitle,songCover, songUrl))
                        Log.i("title",songTitle)
                        Log.i("preview", songUrl)
                    }
                    eventType = parser.next()
                }
                withContext(Dispatchers.Main){
                    listener.finish(data)
                }
            }catch (e: Throwable){
                e.printStackTrace()
            }
        }


    }
}