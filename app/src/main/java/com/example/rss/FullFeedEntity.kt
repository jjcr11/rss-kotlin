package com.example.rss

import java.io.Serializable
import java.util.*

data class FullFeedEntity(var id: Int,
                          var title: String,
                          var source: String,
                          var date: Date,
                          var author: String?,
                          var content: String): Serializable