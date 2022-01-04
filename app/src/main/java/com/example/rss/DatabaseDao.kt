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

    @Query("SELECT * FROM FeedEntity WHERE readed = ${false}")
    fun getUnreadFeeds(): MutableList<FeedEntity>

    @Insert
    fun addFeed(feedEntity: List<FeedEntity>)

    @Query("SELECT * FROM SourceEntity")
    fun getSourcesFeed(): MutableList<SourceFeedRelation>

    @Query("SELECT name FROM SourceEntity WHERE id = :id")
    fun getSourceNameByID(id: Int): String

    @Query("UPDATE FeedEntity set readed = :readed WHERE id = :id")
    fun setRead(id: Int, readed: Boolean)

    @Query("SELECT url FROM FeedEntity WHERE id = :id")
    fun getFeedURL(id: Int): String

    @Query("SELECT id FROM FeedEntity WHERE sourceId = :id  ORDER BY id DESC LIMIT 15")
    fun getFifteenthFeed(id: Int): MutableList<Int>

    @Query("DELETE FROM FeedEntity WHERE id < :id and readed = :readed")
    fun deleteOldFeeds(id: Int, readed: Boolean)
}