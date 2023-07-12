package com.jjcr11.rss.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    fun addSource(source: Source)

    @Insert
    fun addFeed(feed: Feed)

    @Query("SELECT * FROM Source")
    fun getSources(): List<Source>

    @Query("SELECT * FROM Source")
    fun getSourcesWithFeeds(): List<SourceWithFeed>

    @Query("SELECT id FROM Source ORDER BY id DESC LIMIT 1")
    fun getLastSourceId(): Long

    @Query("SELECT * FROM Feed")
    fun getFeeds(): List<Feed>

    @Query("SELECT * FROM Feed ORDER BY date")
    fun getFeedsByDate(): List<Feed>
}