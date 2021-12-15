package com.example.rss

import android.app.Application
import androidx.room.Room

//Class to use database in any class
class DatabaseApplication: Application() {
    companion object {
        lateinit var database: Database
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this,
            Database::class.java,
            "Database")
            .fallbackToDestructiveMigration()
            .build()
    }
}