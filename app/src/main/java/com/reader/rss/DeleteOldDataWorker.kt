package com.reader.rss

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class DeleteOldDataWorker(
    context: Context,
    workerParameters: WorkerParameters
): Worker(context, workerParameters) {
    override fun doWork(): Result {
        deleteData()
        return Result.success()
    }

    private fun deleteData() {
        val sharedPreference = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val index = sharedPreference.getInt("indexDays", 2)
        if(index != 5) {
            val day: Int = when(index) {
                0 -> 1
                1 -> 2
                2 -> 5
                3 -> 10
                else -> 15
            }

            val currentDay = Calendar.getInstance().time
            val actuallyDay = Date(currentDay.time - (86400000 * day))
            val feeds = DatabaseApplication.database.dao().getOldFeeds()
            feeds.forEach {
                if(actuallyDay.time > it.date!!.time) {
                    DatabaseApplication.database.dao().setFeedAsRead(it.id)
                }
            }
        }
    }
}