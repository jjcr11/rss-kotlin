package com.example.rss

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import java.io.Serializable

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
        binding.cpi.visibility = View.GONE
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
        if(feeds.size == 0) {
            binding.root.removeAllViews()
            val view = layoutInflater.inflate(R.layout.empty_saved, null)
            with(view.findViewById<MaterialToolbar>(R.id.mtb).menu) {
                getItem(0).isEnabled = false
                getItem(0).icon = getDrawable(R.drawable.ic_bookmark)
                getItem(1).isEnabled = false
                getItem(1).icon = getDrawable(R.drawable.ic_open_in_new_2)
                getItem(2).isEnabled = false
                getItem(2).icon = getDrawable(R.drawable.ic_share_2)
            }
            binding.root.addView(view)
        }
    }

    override fun onClick(feed: FullFeedEntity, position: Int) {
        val postActivity = Intent(this, PostActivity::class.java)
        postActivity.putExtra("list", feedAdapter.getFeeds() as Serializable)
        postActivity.putExtra("position", position)
        startActivity(postActivity)
    }
}