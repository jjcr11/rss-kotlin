package com.example.rss

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar by deafult
        supportActionBar?.hide()

        feedAdapter = FeedAdapter(getFeeds())
        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        //If the recycler view scrolls then floating action button extends or shrinks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.rv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
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

        var feeds: MutableList<FeedEntity>
        Thread {
            feeds = DatabaseApplication.database.sourceDao().getAllFeeds()
            //sourceAdapter.setSources(sources)
            Log.d("FEEDS: ", feeds.size.toString())
        }.start()

        var sources: MutableList<SourceEntity> = mutableListOf()
        Thread {
            sources = DatabaseApplication.database.sourceDao().getAllSources()
        }.start()
        Timer().schedule(1000){
            if(sources.size != 0) {
                for(source: SourceEntity in sources) {
                    DownloadXmlTask().execute(
                        source.url,
                        source.id.toString()
                    )
                }
            }
        }

    }

    private inner class DownloadXmlTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String): String {
            return try {
                loadXmlFromNetwork(urls[0], urls[1].toInt())
            } catch (e: IOException) {
                "resources.getString(R.string.connection_error)"
            } catch (e: XmlPullParserException) {
                "resources.getString(R.string.xml_error)"
            }
        }

        override fun onPostExecute(result: String) {
            //Log.d("TITLE: ", result)
        }

    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String, sourceId: Int): String {
        val feeds: List<FeedEntity>? = downloadUrl(urlString)?.use { stream ->
            XmlParser().parse(stream, sourceId)
        }

        if (feeds != null) {
            for(feed: FeedEntity in feeds){
                Log.d("TITLE: ", feed.title)
                DatabaseApplication.database.sourceDao().addFeed(feed)
            }
        }

        return feeds?.get(0)?.title.toString()
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
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

    private fun getFeeds(): MutableList<FeedEntity> {
        val feeds = mutableListOf<FeedEntity>()
        /*val f1 = FeedEntity("398 comerciantes en el docenario guadalupano", "El noticiero de Manzanillo", 1)
        val f2 = FeedEntity("Como descargar el certificado COVID de la forma mas sencilla para llevarlo siempre", "Genbeta", 1)
        val f3 = FeedEntity("Endows, a Singapore-based robo-advisor app, raise $25.6M to bring its total funding to $49M as it looks to speed up its hiring for geographic expansion speed up its hiring for geographic expansion", "Techmeme", 1)
        feeds.add(f1)
        feeds.add(f2)
        feeds.add(f3)
        feeds.add(f1)
        feeds.add(f2)
        feeds.add(f3)
        feeds.add(f1)
        feeds.add(f2)
        feeds.add(f3)*/
        return feeds
    }

}