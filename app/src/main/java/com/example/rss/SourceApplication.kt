package com.example.rss

import android.app.Application
import androidx.room.Room

//Class to use database in any class
class SourceApplication: Application() {
    companion object {
        lateinit var database: SourceDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this,
            SourceDatabase::class.java,
            "SourceDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }
}