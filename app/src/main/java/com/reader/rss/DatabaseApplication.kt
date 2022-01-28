package com.reader.rss

import android.app.Application
import androidx.room.Room

//Class to use database in any class
class DatabaseApplication: Application() {
    companion object {
        lateinit var database: com.reader.rss.Database
    }

    override fun onCreate() {
        super.onCreate()
        com.reader.rss.DatabaseApplication.Companion.database = Room.databaseBuilder(this,
            com.reader.rss.Database::class.java,
            "Database")
            .fallbackToDestructiveMigration()
            .build()
    }
}