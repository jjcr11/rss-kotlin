package com.example.rss

import androidx.room.*

@Dao
interface DatabaseDao {
    @Query("SELECT * FROM SourceEntity")
    fun getAllSources(): MutableList<SourceEntity>

    @Insert
    fun addSource(sourceEntity: SourceEntity)

    @Update
    fun updateSource(sourceEntity: SourceEntity)

    @Delete
    fun deleteSource(sourceEntity: SourceEntity)
}