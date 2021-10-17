package com.example.top10downloaderapp

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.IllegalStateException

class XmlParser {
    private val ns: String? = null

    fun parse(inputStream: InputStream): List<App>{
        inputStream.use {
            inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readRssFeed(parser)
        }
    }

    private fun readRssFeed(parser: XmlPullParser): List<App> {
        val questions = mutableListOf<App>()

        parser.require(XmlPullParser.START_TAG, ns, "feed")
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.eventType != XmlPullParser.START_TAG){
                continue
            }
            if(parser.name == "entry"){
                parser.require(XmlPullParser.START_TAG, ns, "entry")
                var title: String? = null
                var id: String? = ""
                var summary: String? = ""
                var image: String? = ""
                var price: String? = ""
                while (parser.next() != XmlPullParser.END_TAG){
                    if(parser.eventType != XmlPullParser.START_TAG){
                        continue
                    }
                    when(parser.name){
                        "im:name" -> title = readTitle(parser)
                        "summary" -> summary = readSummary(parser)
                        "id" -> id = readId(parser)
                        "im:image" -> image = readImgUrl(parser)
                        "im:price" -> price = readPrice(parser)
                        else -> skip(parser)
                    }
                }
                questions.add(App(title, summary, id, image,price))
            } else {
                skip(parser)
            }
        }
        return questions
    }

    private fun readPrice(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "im:price")
        val price = readAmountAttribute(parser)
        parser.require(XmlPullParser.END_TAG, ns, "im:price")
        return price
    }

    private fun readImgUrl(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "im:image")
        val imgUrl = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "im:image")
        return imgUrl
    }

    private fun skip(parser: XmlPullParser) {
        if(parser.eventType != XmlPullParser.START_TAG){
            throw IllegalStateException()
        }
            var depth = 1
            while (depth != 0){
                when(parser.next()){
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
    }

    private fun readTitle(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "im:name")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "im:name")
        return title
    }

    private fun readSummary(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "summary")
        val summary = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "summary")
        return summary
    }

    private fun readId(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "id")
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "id")
        return id
    }

//    private fun readSummary(parser: XmlPullParser): String? {
//        var name : String? = ""
//        parser.require(XmlPullParser.START_TAG, ns, "author")
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            when (parser.name){
//                "name" -> {name = readText(parser)}
//                else -> skip(parser)
//            }
//        }
//        return name
//    }

//    private fun readLink(parser: XmlPullParser): String? {
//        parser.require(XmlPullParser.START_TAG, ns, "link")
//        val title = readText(parser)
//        parser.require(XmlPullParser.END_TAG, ns, "link")
//        return title
//    }

    private fun readText(parser: XmlPullParser): String? {
        var result = ""
        if(parser.next() == XmlPullParser.TEXT){
            result = parser.text
            parser.nextTag()
        }
        return result
    }


    private fun readAmountAttribute(parser: XmlPullParser): String? {
        var result = ""
        result = parser.getAttributeValue(null,"amount")
        if(result.toDouble() != 0.00){
            result = "$result USD"
        }
        else{result= "Free"}
        if(parser.next() == XmlPullParser.TEXT){
            parser.nextTag()
        }
        return result
    }





}
