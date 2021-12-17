package com.example.rss

import androidx.room.Entity
import androidx.room.PrimaryKey

//Data class to be used by SourceAdapter
@Entity(tableName = "SourceEntity")
data class SourceEntity(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                        var name: String?,
                        var url: String?)