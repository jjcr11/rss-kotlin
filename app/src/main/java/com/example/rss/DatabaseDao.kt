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

    @Query("SELECT FeedEntity.id, title, name as source, date, author, content FROM FeedEntity INNER JOIN SourceEntity ON FeedEntity.sourceId = SourceEntity.id  WHERE FeedEntity.readed = ${false} ORDER BY date LIMIT 50")
    fun getUnreadFeeds(): MutableList<FullFeedEntity>

    @Insert
    fun addFeed(feedEntity: FeedEntity)

    @Query("SELECT * FROM SourceEntity")
    fun getSourcesFeed(): MutableList<SourceFeedRelation>

    @Query("SELECT name FROM SourceEntity WHERE id = :id")
    fun getSourceNameByID(id: Int): String

    @Query("UPDATE FeedEntity set readed = :readed WHERE id = :id")
    fun setRead(id: Int, readed: Boolean)

    @Query("SELECT url FROM FeedEntity WHERE id = :id")
    fun getFeedURL(id: Int): String

    @Query("SELECT id FROM FeedEntity WHERE sourceId = :id and readed = ${true} and saved = ${false}")
    fun getFeedsById(id: Int): MutableList<Int>

    @Query("DELETE FROM FeedEntity WHERE id = :id")
    fun deleteOldFeeds(id: Int)

    @Query("UPDATE FeedEntity set saved = :saved where id = :id")
    fun setSaved(id: Int, saved: Boolean)

    @Query("SELECT saved FROM FeedEntity where id = :id")
    fun getSaved(id: Int): Boolean

    @Query("SELECT FeedEntity.id, title, name as source, date, author, content FROM FeedEntity INNER JOIN SourceEntity ON FeedEntity.sourceId = SourceEntity.id  WHERE FeedEntity.saved = ${true} ORDER BY date LIMIT 50")
    fun getSavedFeeds(): MutableList<FullFeedEntity>
}