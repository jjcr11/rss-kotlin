package com.jjcr11.rss.data.model

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun toDate(time: Long?): Date? = time?.let { Date(it) }

    @TypeConverter
    fun toLong(date: Date?): Long? = date?.time?.toLong()

}