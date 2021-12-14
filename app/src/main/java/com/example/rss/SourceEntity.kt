package com.example.rss

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException

//Data class to be used by SourceAdapter
@Entity(tableName = "SourceEntity")
data class SourceEntity(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                        var name: String?,
                        var url: String?)

private val ns: String? = null

@Throws(XmlPullParserException::class, IOException::class)
fun readChannel(parser: XmlPullParser): SourceEntity {
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
fun skip(parser: XmlPullParser) {
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