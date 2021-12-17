package com.example.rss

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException
import java.util.*

//Data class to be used by FeedAdapter
@Entity(tableName = "FeedEntity", indices = arrayOf(Index(value = ["title"], unique = true)))
data class FeedEntity(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                      var title: String,
                      var url: String,
                      var author: String?,
                      var date: Date?,
                      var content: String,
                      var sourceId: Int)

private val ns: String? = null

@Throws(XmlPullParserException::class, IOException::class)
fun readItem(parser: XmlPullParser, sourceId: Int): FeedEntity {
    parser.require(XmlPullParser.START_TAG, ns, "item")
    var title = ""
    var link = ""
    var author: String? = null
    var date: Date? = null
    var content = ""
    while(parser.next() != XmlPullParser.END_TAG) {
        if(parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when(parser.name) {
            "title" -> title = readTitle(parser)
            "link" -> link = readLink(parser)
            "dc:creator" -> author = readAuthor(parser)
            "pubDate" -> date = readDate(parser)
            "content:encoded" -> content = readContent(parser)
            else -> skip(parser)
        }
    }
    return FeedEntity(title = title, url = link, author = author, date = date, content = content, sourceId = sourceId)
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
    val date = Date(title)
    parser.require(XmlPullParser.END_TAG, ns, "pubDate")
    return date
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readContent(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "content:encoded")
    val title = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "content:encoded")
    return title
}

@Throws(XmlPullParserException::class, IOException::class)
fun skipItem(parser: XmlPullParser) {
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
