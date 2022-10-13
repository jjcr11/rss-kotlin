package com.reader.rss

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.reader.rss.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), FeedAdapterOnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var flag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cpi.visibility = View.GONE

        val workRequest = PeriodicWorkRequestBuilder<DeleteOldDataWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager
            .getInstance(this)
            .enqueue(workRequest)

        val sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)
        when(sharedPreference.getInt("theme", 2)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        binding.mtb.menu.getItem(1).icon = if(sharedPreference.getBoolean("sort", true)) {
            ContextCompat.getDrawable(this, R.drawable.ic_sort)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_sort_2)
        }

        feedAdapter = FeedAdapter(mutableListOf(), sharedPreference.getInt("cornerRadius", 0), this)

        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            deleteData()
        }
        getData(sharedPreference.getBoolean("sort", true))

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

        binding.nv.menu.apply {
            getItem(0).setOnMenuItemClickListener {
                startActivity(Intent(baseContext, SettingsActivity::class.java))
                true
            }

            getItem(1).setOnMenuItemClickListener {
                val savedActivity = Intent(baseContext, SavedActivity::class.java)
                savedActivity.putExtra("theme", binding.fabe.currentTextColor)
                startActivity(savedActivity)
                true
            }

            getItem(2).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jjcr11/rss-kotlin")))
                true
            }

            getItem(3).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/jjcr11")))
                true
            }
        }

        binding.mtb.menu.getItem(1).setOnMenuItemClickListener {
            sharedPreference.edit().putBoolean(
                "sort",
                !sharedPreference.getBoolean("sort", true)
            ).apply()
            binding.mtb.menu.getItem(1).icon = if(sharedPreference.getBoolean("sort", true)) {
                ContextCompat.getDrawable(this, R.drawable.ic_sort)
            } else {
                ContextCompat.getDrawable(this, R.drawable.ic_sort_2)
            }
            getFeeds(sharedPreference.getBoolean("sort", true))

            true
        }

        binding.srl.setOnRefreshListener {
            flag = false
            getData(sharedPreference.getBoolean("sort", true))
        }
    }

    private fun deleteData() {
        val sources = DatabaseApplication.database.dao().getAllSources()
        for(source in sources) {
            val feedsById = DatabaseApplication.database.dao().getFeedsId(source.id)
            if(feedsById.size > source.count) {
                val subList = feedsById.subList(0, (feedsById.size - source.count))
                for(sub in subList) {
                    DatabaseApplication.database.dao().deleteFeedById(sub)
                }
            }
        }
    }

    private fun getData(sort: Boolean) {
        val connectivityManager = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            var sources: MutableList<SourceEntity> = mutableListOf()
            CoroutineScope(Dispatchers.IO).launch {
                val asyncJob = async {
                    sources = DatabaseApplication.database.dao().getAllSources()
                }
                asyncJob.await()
                if(flag) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.cpi.visibility = View.VISIBLE
                    }
                }
                if(sources.size == 0) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.cpi.visibility = View.GONE
                        binding.srl.isRefreshing = false
                    }
                } else {
                    for (source in sources) {
                        downloadXmlTask(source.url, source.id, sort)
                    }
                }
            }
        } else {
            getFeeds(sort)
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadXmlTask(url: String?, id: Int, sort: Boolean) {
        val feeds = loadXmlFromNetwork(url, id)
        DatabaseApplication.database.dao().setSourceCount(id, feeds.size)
        for(feed in feeds) {
            try {
                DatabaseApplication.database.dao().addFeed(feed)
            } catch (e: SQLiteConstraintException) {

            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            getFeeds(sort)
        }
    }


    private fun loadXmlFromNetwork(urlString: String?, sourceId: Int): List<FeedEntity> {
        var feeds = listOf<FeedEntity>()
        downloadUrl(urlString)?.use { stream ->
            feeds = XmlParser().parse(stream, sourceId)
        }!!
        return feeds
    }

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

    private fun getFeeds(sort: Boolean) {
        var feeds: MutableList<FullFeedEntity> = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            val asyncJob = async {
                feeds = if(sort) {
                    DatabaseApplication.database.dao().getUnreadFeeds()
                } else {
                    DatabaseApplication.database.dao().getUnreadFeedsDesc()
                }
            }
            asyncJob.await()
            CoroutineScope(Dispatchers.Main).launch {
                if (flag) {
                    binding.cpi.visibility = View.GONE
                }
                binding.srl.isRefreshing = false
                feedAdapter.setFeeds(feeds)
                binding.mtb.menu.getItem(0).title = feedAdapter.itemCount.toString()
            }
        }
    }

    override fun onClick(feed: FullFeedEntity, position: Int) {
        val postActivity = Intent(this, PostActivity::class.java)
        val sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)
        postActivity.putExtra("position", position)
        postActivity.putExtra("sort", sharedPreference.getBoolean("sort", true))
        postActivity.putExtra("theme", binding.fabe.currentTextColor)
        startActivity(postActivity)
    }
}