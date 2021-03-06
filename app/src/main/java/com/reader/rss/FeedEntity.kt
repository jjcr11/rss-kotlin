package com.reader.rss

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

//Data class to be used by FeedAdapter
@Entity(tableName = "FeedEntity", indices = [Index(value = ["url"], unique = true)])
data class FeedEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    var url: String,
    var author: String?,
    var date: Date?,
    var content: String,
    var sourceId: Int,
    var read: Boolean = false,
    var saved: Boolean = false
)