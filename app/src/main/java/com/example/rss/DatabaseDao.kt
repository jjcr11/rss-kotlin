package com.example.rss

import androidx.room.*

@Dao
interface DatabaseDao {

    @Insert
    fun addSource(sourceEntity: SourceEntity)

    @Query("SELECT * FROM SourceEntity")
    fun getAllSources(): MutableList<SourceEntity>

    @Query("DELETE FROM SourceEntity WHERE id = :id")
    fun deleteSource(id: Int)

    @Query("SELECT name FROM SourceEntity WHERE id = :id")
    fun getSourceNameByID(id: Int): String

    //--------------------------------------------------

    @Insert
    fun addFeed(feedEntity: FeedEntity)

    @Delete
    fun deleteFeeds(Feeds: MutableList<FeedEntity>)

    @Query("DELETE FROM FeedEntity WHERE id = :id")
    fun deleteFeedById(id: Int)

    @Query("UPDATE FeedEntity set read = 1 WHERE id = :id")
    fun setFeedAsRead(id: Int)

    @Query("SELECT url FROM FeedEntity WHERE id = :id")
    fun getFeedUrl(id: Int): String

    @Query("SELECT id FROM FeedEntity WHERE sourceId = :id and read = 1 and saved = 0 ORDER BY date")
    fun getFeedsId(id: Int): MutableList<Int>

    @Query("SELECT FeedEntity.id, title, name as source, date, author, content FROM FeedEntity INNER JOIN SourceEntity ON FeedEntity.sourceId = SourceEntity.id  WHERE FeedEntity.read = 0 ORDER BY date LIMIT 50")
    fun getUnreadFeeds(): MutableList<FullFeedEntity>

    @Query("UPDATE FeedEntity set saved = :saved where id = :id")
    fun setFeedAsSavedOrUnsaved(id: Int, saved: Boolean)

    @Query("SELECT saved FROM FeedEntity where id = :id")
    fun getFeedSaved(id: Int): Boolean

    @Query("SELECT FeedEntity.id, title, name as source, date, author, content FROM FeedEntity INNER JOIN SourceEntity ON FeedEntity.sourceId = SourceEntity.id  WHERE FeedEntity.saved = 1 ORDER BY date LIMIT 50")
    fun getAllFeedsSaved(): MutableList<FullFeedEntity>

    @Query("SELECT * FROM FeedEntity WHERE sourceId = :id")
    fun getAllFeedsById(id: Int): MutableList<FeedEntity>
}