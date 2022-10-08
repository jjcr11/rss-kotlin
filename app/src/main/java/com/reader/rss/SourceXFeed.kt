package com.reader.rss

data class SourceXFeed(
    val id: Int,
    val name: String,
    val url: String,
    val count: Int,
    var feeds: MutableList<FeedEntity> = mutableListOf()
)
