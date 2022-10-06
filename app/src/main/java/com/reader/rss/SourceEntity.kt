package com.reader.rss

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//Data class to be used by SourceAdapter
@Entity(tableName = "SourceEntity", indices = [Index(value = ["url"], unique = true)])
data class SourceEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String?,
    var url: String?,
    var count: Int = 0
    )