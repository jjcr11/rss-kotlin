package com.example.rss

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding
import kotlinx.coroutines.*
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
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cpi.visibility = View.GONE

        Thread {
            try {
                var sources = DatabaseApplication.database.dao().getSources()
                for(source in sources) {
                    var feedsById = DatabaseApplication.database.dao().getFeedsById(source.id)
                    var count = 0
                    var size = feedsById.size
                    while(size > 26) {
                        DatabaseApplication.database.dao().deleteOldFeeds(feedsById[count])
                        size -= 1
                        count += 1
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

        binding.nv.menu.getItem(1).setOnMenuItemClickListener {
            val savedActivity = Intent(this, SavedActivity::class.java)
            startActivity(savedActivity)
            true
        }


    }

    private fun getData() {
        val sources: MutableList<SourceEntity>
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            runBlocking(Dispatchers.IO) {
                sources = DatabaseApplication.database.dao().getSources()
            }
            if(sources.size > 0) {
                binding.cpi.visibility = View.VISIBLE
                for(source: SourceEntity in sources) {
                    //Log.d("SOURCES", source.name!!)
                    downloadXmlTask(source.url, source.id)
                }
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
        /*Handler(Looper.myLooper()!!).postDelayed({
            getFeeds()
        }, 2000)*/
    }

    private fun downloadXmlTask(url: String?, id: Int) {
        var feeds: List<FeedEntity> = mutableListOf()
        GlobalScope.launch {
            feeds = loadXmlFromNetwork(url, id)
            runBlocking(Dispatchers.IO) {
                for(feed in feeds) {
                    try {
                        DatabaseApplication.database.dao().addFeed(feed)
                    } catch (e: SQLiteConstraintException) {
                        Log.d("TITLE: ", e.toString())
                    }
                }
            }
            runBlocking(Dispatchers.Main) {
                getFeeds()
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private suspend fun loadXmlFromNetwork(urlString: String?, sourceId: Int): List<FeedEntity> {
        val feeds: List<FeedEntity> = withContext(Dispatchers.IO) {
            downloadUrl(urlString)?.use { stream ->
                XmlParser().parse(stream, sourceId)
            }!!
        }
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

    private fun getFeeds() {
        var feeds: MutableList<FullFeedEntity>
        runBlocking(Dispatchers.IO) {
            feeds = DatabaseApplication.database.dao().getUnreadFeeds()
        }
        binding.cpi.visibility = View.GONE
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