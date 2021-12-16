package com.example.rss

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class XmlParser {

    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): SourceEntity? {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream, sourceId: Int): List<FeedEntity> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser, sourceId)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): SourceEntity? {
        var channel: SourceEntity? = null
        parser.require(XmlPullParser.START_TAG, ns, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "channel") {

                channel = readChannel(parser)
            } else {
                skip(parser)
            }
        }
        return channel
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser, sourceId: Int): List<FeedEntity> {
        //val entries = mutableListOf<Entry>()
        var items = mutableListOf<FeedEntity>()
        parser.require(XmlPullParser.START_TAG, ns, "rss")
        parser.nextTag()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "item") {
                Log.d("MMMMMMMMMMM", "MMMMMMMMMMMY")
                items.add(readItem(parser, sourceId))
            } else {
                skip(parser)
            }
        }
        return items
    }

}