package com.reader.rss

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SourceEntity::class, FeedEntity::class], version = 10)
@TypeConverters(DatabaseConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun dao(): DatabaseDao
}