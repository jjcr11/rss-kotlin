package com.example.rss

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), FeedAdapterOnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread {
            try {
                var sources = DatabaseApplication.database.dao().getSources()
                for(source in sources) {
                    var feedsById = DatabaseApplication.database.dao().getFeedsById(source.id)
                    if(feedsById.size > 25) {
                        for(feed in feedsById) {
                            DatabaseApplication.database.dao().deleteOldFeeds(feed)
                        }
                    }
                }
            } catch (e: SQLiteConstraintException) {
                Log.d("EXCEPTION", e.toString())
            }
        }.start()

        //Hide the action bar by default
        supportActionBar?.hide()

        val sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreference.getInt("cornerRadius", 0)
        feedAdapter = FeedAdapter(mutableListOf(), sharedPreference.getInt("cornerRadius", 0), this)

        linearLayoutManager = LinearLayoutManager(this)

        getData()
        //getFeeds()

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        //If the recycler view scrolls then floating action button extends or shrinks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.rv.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
                if(oldScrollY >= 0) {
                    binding.fabe.extend()
                } else {
                    binding.fabe.shrink()
                }
            }
        }

        //Get first item of top_app_bar.xml
        binding.mtb.menu.getItem(0).title = feedAdapter.itemCount.toString()

        binding.fabe.setOnClickListener {
            val sourceActivity = Intent(this, SourceActivity::class.java)
            startActivity(sourceActivity)
        }

        binding.mtb.setNavigationOnClickListener {
            binding.dl.openDrawer(GravityCompat.START)
        }

        binding.nv.menu.getItem(0).setOnMenuItemClickListener {
            val settingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(settingsActivity)
            true
        }


    }

    private fun getData() {
        var sources: MutableList<SourceEntity> = mutableListOf()
        Thread {
            sources = DatabaseApplication.database.dao().getSources()
            if(sources.size > 0) {
                for(source: SourceEntity in sources) {
                    downloadXmlTask(source.url, source.id)
                }
            }
        }.start()
        Handler(Looper.myLooper()!!).postDelayed({
            getFeeds()
        }, 2000)
    }

    private fun downloadXmlTask(url: String?, id: Int) {
        var feeds: List<FeedEntity> = mutableListOf()
        feeds = loadXmlFromNetwork(url, id)
        for(feed in feeds) {
            try {
                DatabaseApplication.database.dao().addFeed(feed)
            } catch (e: SQLiteConstraintException) {
                Log.d("TITLE: ", e.toString())
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
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }

    private fun getFeeds() {
        var feeds: MutableList<FullFeedEntity> = mutableListOf()
        val t = Thread {
            feeds = DatabaseApplication.database.dao().getUnreadFeeds()

        }
        t.start()
        t.join()
        feedAdapter.setFeeds(feeds)
    }

    override fun onClick(feed: FullFeedEntity, position: Int) {
        //Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()
        val postActivity = Intent(this, PostActivity::class.java)
        postActivity.putExtra("list", feedAdapter.getFeeds() as Serializable)
        postActivity.putExtra("position", position)
        startActivity(postActivity)
    }
}