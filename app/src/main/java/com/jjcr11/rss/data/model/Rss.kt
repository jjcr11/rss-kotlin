package com.jjcr11.rss.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class Rss @JvmOverloads constructor(
    @field:Element(name = "title")
    @param:Element(name = "title")
    @field:Path("channel")
    @param:Path("channel")
    var title: String? = null,

    @field:ElementList(name = "item", inline = true, required = false)
    @param:ElementList(name = "item", inline = true, required = false)
    @field:Path("channel")
    @param:Path("channel")
    var items: List<Item>? = null,
)

@Root(name = "rss", strict = false)
data class RssContent @JvmOverloads constructor(
    @field:Element(name = "title")
    @param:Element(name = "title")
    @field:Path("channel")
    @param:Path("channel")
    var title: String? = null,

    @field:ElementList(name = "item", inline = true, required = false)
    @param:ElementList(name = "item", inline = true, required = false)
    @field:Path("channel")
    @param:Path("channel")
    var items: List<ItemContent>? = null,
)

@Entity
data class Source(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var title: String,
    var link: String,
)