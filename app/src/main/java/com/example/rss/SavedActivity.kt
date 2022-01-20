package com.example.rss

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding
import java.io.Serializable

class SavedActivity : AppCompatActivity(), FeedAdapterOnClickListener {

    lateinit var binding: ActivityMainBinding
    lateinit var feedAdapter: FeedAdapter
    lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.fabe.hide()
        binding.mtb.visibility = View.GONE
        binding.cpi.visibility = View.GONE

        val sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreference.getInt("cornerRadius", 0)
        feedAdapter = FeedAdapter(mutableListOf(), sharedPreference.getInt("cornerRadius", 0), this)

        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        getFeeds()
    }

    private fun getFeeds() {
        var feeds: MutableList<FullFeedEntity> = mutableListOf()
        val t = Thread {
            feeds = DatabaseApplication.database.dao().getAllFeedsSaved()

        }
        t.start()
        t.join()
        feedAdapter.setFeeds(feeds)
    }

    override fun onClick(feed: FullFeedEntity, position: Int) {
        val postActivity = Intent(this, PostActivity::class.java)
        postActivity.putExtra("list", feedAdapter.getFeeds() as Serializable)
        postActivity.putExtra("position", position)
        startActivity(postActivity)
    }
}