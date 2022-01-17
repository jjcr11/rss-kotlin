package com.example.rss

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SourceEntity::class, FeedEntity::class], version = 8)
@TypeConverters(DatabaseConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun dao(): DatabaseDao
}