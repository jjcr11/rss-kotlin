package com.reader.rss

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class UploadWorker(
    context: Context,
    workerParameters: WorkerParameters
): Worker(context, workerParameters) {
    override fun doWork(): Result {
        getData()
        return Result.success()
    }

    private fun getData() {
        val sources: MutableList<SourceEntity>
        sources = DatabaseApplication.database.dao().getAllSources()
        if(sources.size > 0) {
            for(source: SourceEntity in sources) {
                downloadXmlTask(source.url, source.id)
            }
        }
    }

    private fun downloadXmlTask(url: String?, id: Int) {
        var feeds: List<FeedEntity> = mutableListOf()
        feeds = loadXmlFromNetwork(url, id)
        for(feed in feeds) {
            try {
                DatabaseApplication.database.dao().addFeed(feed)
            } catch (e: SQLiteConstraintException) {
                Log.d("DownloadXmlTaskWorker", e.toString())
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String?, sourceId: Int): List<FeedEntity> {
        val feeds: List<FeedEntity> = downloadUrl(urlString)?.use { stream ->
            XmlParser().parse(stream, sourceId)
        }!!
        return feeds
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String?): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 15000
            connectTimeout = 20000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }
}