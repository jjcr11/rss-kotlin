package com.example.rss

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(SourceEntity::class, FeedEntity::class), version = 3)
@TypeConverters(DatabaseConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun sourceDao(): DatabaseDao
}