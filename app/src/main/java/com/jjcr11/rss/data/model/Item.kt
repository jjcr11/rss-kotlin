package com.jjcr11.rss.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.Date

@Root(name = "item", strict = false)
data class Item @JvmOverloads constructor(
    @field:Element(name = "title")
    @param:Element(name = "title")
    var title: String? = null,

    @field:Element(name = "link")
    @param:Element(name = "link")
    var link: String? = null,

    @field:Element(name = "description")
    @param:Element(name = "description")
    var description: String? = null,

    @field:Element(name = "pubDate")
    @param:Element(name = "pubDate")
    var date: String? = null
)

@Root(name = "item", strict = false)
data class ItemContent @JvmOverloads constructor(
    @field:Element(name = "title")
    @param:Element(name = "title")
    var title: String? = null,

    @field:Element(name = "link")
    @param:Element(name = "link")
    var link: String? = null,

    @field:Element(name = "description")
    @param:Element(name = "description")
    var description: String? = null,

    @field:Element(name = "pubDate")
    @param:Element(name = "pubDate")
    var date: String? = null,

    @field:Element(name = "encoded")
    @param:Element(name = "encoded")
    var content: String? = null
)

@Entity
@Parcelize
data class Feed(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val sourceId: Long,
    val title: String,
    val link: String,
    val description: String,
    val date: Date,
    var read: Boolean = false,
    var saved: Boolean = false
) : Parcelable