package com.example.rss

import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), FeedAdapterOnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar by default
        supportActionBar?.hide()

        feedAdapter = FeedAdapter(mutableListOf(), mutableListOf(), this)

        linearLayoutManager = LinearLayoutManager(this)

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


    }

    override fun onResume() {
        super.onResume()
        getData()
        getFeeds()
    }

    private fun getData() {
        var sources: MutableList<SourceEntity> = mutableListOf()
        val t = Thread {
            sources = DatabaseApplication.database.dao().getSources()
        }
        t.start()
        t.join()
        if(sources.size > 0) {
            for(source: SourceEntity in sources) {
                downloadXmlTask(source.url, source.id)
            }
        }
    }

    private fun downloadXmlTask(url: String?, id: Int) {
        var feeds: List<FeedEntity> = mutableListOf()
        val t = Thread {
            feeds = loadXmlFromNetwork(url, id)
            for(feed: FeedEntity in feeds) {
                try {
                    DatabaseApplication.database.dao().addFeed(feed)
                } catch (e: SQLiteConstraintException) {
                    Log.d("TITLE: ", e.toString())
                }
            }
        }
        t.start()
        t.join()
        //for(feed: FeedEntity in feeds) {
        //    feedAdapter.add(feed)
        //}
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
        var feeds: MutableList<FeedEntity> = mutableListOf()
        val sources: MutableList<String> = mutableListOf()
        val t = Thread {
            feeds = DatabaseApplication.database.dao().getUnreadFeeds()
            for(feed: FeedEntity in feeds) {
                sources.add(
                    DatabaseApplication.database.dao().getSourceNameByID(feed.sourceId)
                )
            }
        }
        t.start()
        t.join()
        feedAdapter.setFeeds(feeds)
        feedAdapter.setSources(sources)
    }

    override fun onClick(feed: FeedEntity, position: Int) {
        //Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()
        val postActivity = Intent(this, PostActivity::class.java)
        postActivity.putExtra("list", feedAdapter.getFeeds() as Serializable)
        postActivity.putExtra("position", position)
        startActivity(postActivity)
    }
}