package com.example.rss

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

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
        val items = mutableListOf<FeedEntity>()
        parser.require(XmlPullParser.START_TAG, ns, "rss")
        parser.nextTag()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "item") {
                items.add(readItem(parser, sourceId))
            } else {
                skip(parser)
            }
        }
        return items
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readChannel(parser: XmlPullParser): SourceEntity {
        parser.require(XmlPullParser.START_TAG, ns, "channel")
        var name: String? = null
        var url: String? = null
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when(parser.name) {
                "title" -> name = readName(parser)
                "link" -> url = readUrl(parser)
                else -> skip(parser)
            }
        }
        return SourceEntity(name = name, url = url)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readUrl(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "link")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "link")
        return title
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if(parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while(depth != 0) {
            when(parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if(parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser, sourceId: Int): FeedEntity {
        parser.require(XmlPullParser.START_TAG, ns, "item")
        var title = ""
        var link = ""
        var author: String? = null
        var date: Date? = null
        var content = ""
        var description = ""
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when(parser.name) {
                "title" -> title = readTitle(parser)
                "link" -> link = readLink(parser)
                "dc:creator" -> author = readAuthor(parser)
                "pubDate" -> date = readDate(parser)
                "content:encoded" -> content = readContent(parser, "content:encoded")
                "description" -> description = readContent(parser, "description")
                else -> skip(parser)
            }
        }
        if(content.length > description.length) {
            return FeedEntity(title = title, url = link, author = author, date = date, content = content, sourceId = sourceId)
        } else {
            return FeedEntity(title = title, url = link, author = author, date = date, content = description, sourceId = sourceId)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readLink(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "link")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "link")
        return title
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAuthor(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "dc:creator")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "dc:creator")
        return title
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readDate(parser: XmlPullParser): Date {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate")
        val title = readText(parser)
        val formatterParser = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
        val date = formatterParser.parse(title)
        parser.require(XmlPullParser.END_TAG, ns, "pubDate")
        return date!!
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readContent(parser: XmlPullParser, name: String): String {
        parser.require(XmlPullParser.START_TAG, ns, name)
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, name)
        return title
    }
}