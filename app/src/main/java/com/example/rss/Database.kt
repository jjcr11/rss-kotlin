package com.example.rss

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(SourceEntity::class), version = 2)
abstract class Database: RoomDatabase() {
    abstract fun sourceDao(): DatabaseDao
}