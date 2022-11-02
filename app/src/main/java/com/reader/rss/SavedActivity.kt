package com.reader.rss

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reader.rss.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar

class SavedActivity : AppCompatActivity(), FeedAdapterOnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.fabe.hide()
        binding.mtb.visibility = View.GONE
        binding.root.removeView(binding.nv)
        binding.srl.isEnabled = false

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
        postActivity.putExtra("position", position)
        postActivity.putExtra("saved", true)
        postActivity.putExtra("theme", intent.getIntExtra("theme", -14408668))
        startActivity(postActivity)
    }
}