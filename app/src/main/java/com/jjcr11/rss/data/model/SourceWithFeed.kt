package com.jjcr11.rss.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SourceWithFeed(
    @Embedded val source: Source,
    @Relation(
        parentColumn = "id",
        entityColumn = "sourceId"
    )
    val feeds: MutableList<Feed>
)