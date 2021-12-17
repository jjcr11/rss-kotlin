package com.example.rss

import androidx.room.*

@Dao
interface DatabaseDao {
    @Query("SELECT * FROM SourceEntity")
    fun getAllSources(): MutableList<SourceEntity>

    @Query("SELECT * FROM FeedEntity")
    fun getAllFeeds(): MutableList<FeedEntity>

    @Insert
    fun addSource(sourceEntity: SourceEntity)

    @Insert
    fun addFeed(feedEntity: FeedEntity)

    @Update
    fun updateSource(sourceEntity: SourceEntity)

    @Delete
    fun deleteSource(sourceEntity: SourceEntity)

    @Query("SELECT * FROM SourceEntity")
    fun getAllSourcesFeed(): MutableList<SourceFeedRelation>

    @Query("SELECT name FROM SourceEntity WHERE id = :id")
    fun getSourceNameByID(id: Int): String
}