package com.example.rss

import androidx.room.*

@Dao
interface DatabaseDao {
    @Query("SELECT * FROM SourceEntity")
    fun getSources(): MutableList<SourceEntity>

    @Insert
    fun addSource(sourceEntity: SourceEntity)

    @Update
    fun updateSource(sourceEntity: SourceEntity)

    @Delete
    fun deleteSource(sourceEntity: SourceEntity)

    @Query("SELECT * FROM FeedEntity")
    fun getFeeds(): MutableList<FeedEntity>

    @Insert
    fun addFeed(feedEntity: FeedEntity)

    @Query("SELECT * FROM SourceEntity")
    fun getSourcesFeed(): MutableList<SourceFeedRelation>

    @Query("SELECT name FROM SourceEntity WHERE id = :id")
    fun getSourceNameByID(id: Int): String
}