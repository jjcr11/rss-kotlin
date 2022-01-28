package com.reader.rss

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [com.reader.rss.SourceEntity::class, com.reader.rss.FeedEntity::class], version = 9)
@TypeConverters(com.reader.rss.DatabaseConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun dao(): com.reader.rss.DatabaseDao
}