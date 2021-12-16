package com.example.rss

import androidx.room.Embedded
import androidx.room.Relation

data class SourceFeedRelation(
    @Embedded var source: SourceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sourceId"
    )
    var feeds: FeedEntity
)
