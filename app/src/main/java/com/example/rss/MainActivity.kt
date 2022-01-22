package com.example.rss

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class MainActivity : AppCompatActivity(), FeedAdapterOnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar by default
        supportActionBar?.hide()
        binding.cpi.visibility = View.GONE

        val sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreference.getInt("cornerRadius", 0)
        if(sharedPreference.getBoolean("theme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        feedAdapter = FeedAdapter(mutableListOf(), sharedPreference.getInt("cornerRadius", 0), this)

        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        deleteData()
        getData()

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

        binding.fabe.setOnClickListener {
            startActivity(Intent(this, SourceActivity::class.java))
        }

        binding.mtb.setNavigationOnClickListener {
            binding.dl.openDrawer(GravityCompat.START)
        }

        with(binding.nv.menu) {

            getItem(0).setOnMenuItemClickListener {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }

            getItem(1).setOnMenuItemClickListener {
                startActivity(Intent(context, SavedActivity::class.java))
                true
            }

            getItem(2).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jjcr11/")))
                true
            }

            getItem(3).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/jjcr11")))
                true
            }
        }

        binding.srl.setOnRefreshListener {
            getData()
        }
    }

    private fun deleteData() {
        Thread {
            try {
                val sources = DatabaseApplication.database.dao().getAllSources()
                for(source in sources) {
                    val feedsById = DatabaseApplication.database.dao().getFeedsId(source.id)
                    var count = 0
                    var size = feedsById.size
                    while(size > 26) {
                        DatabaseApplication.database.dao().deleteFeedById(feedsById[count])
                        size -= 1
                        count += 1
                    }
                }
            } catch (e: Exception) {
                Log.d("DeleteData", e.toString())
            }
        }.start()
    }

    private fun getData() {
        val sources: MutableList<SourceEntity>
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            runBlocking(Dispatchers.IO) {
                sources = DatabaseApplication.database.dao().getAllSources()
            }
            if(sources.size > 0) {
                binding.cpi.visibility = View.VISIBLE
                binding.srl.isRefreshing = false
                for(source: SourceEntity in sources) {
                    downloadXmlTask(source.url, source.id)
                }
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
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
                        Log.d("DownloadXmlTask", e.toString())
                    }
                }
            }
            runBlocking(Dispatchers.Main) {
                try {
                    getFeeds()
                    binding.mtb.menu.getItem(0).title = feedAdapter.itemCount.toString()
                } catch (e: Exception) {
                    Log.d("DownloadGetFeeds", e.toString())
                }
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
        val postActivity = Intent(this, PostActivity::class.java)
        postActivity.putExtra("list", feedAdapter.getFeeds() as Serializable)
        postActivity.putExtra("position", position)
        startActivity(postActivity)
    }
}