package com.example.rss

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(SourceEntity::class), version = 1)
abstract class SourceDatabase: RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}