package com.example.rss

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        feedAdapter = FeedAdapter(getFeeds())

        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        binding.mtb.menu.getItem(0).title = feedAdapter.itemCount.toString()


    }

    private fun getFeeds(): MutableList<Feed> {
        val feeds = mutableListOf<Feed>()
        val f1 = Feed("398 comerciantes en el docenario guadalupano", "El noticiero de Manzanillo", 1)
        val f2 = Feed("Como descargar el certificado COVID de la forma mas sencilla para llevarlo siempre", "Genbeta", 1)
        val f3 = Feed("Endows, a Singapore-based robo-advisor app, raise $25.6M to bring its total funding to $49M as it looks to speed up its hiring for geographic expansion speed up its hiring for geographic expansion", "Techmeme", 1)
        feeds.add(f1)
        feeds.add(f2)
        feeds.add(f3)
        feeds.add(f1)
        feeds.add(f2)
        feeds.add(f3)
        feeds.add(f1)
        feeds.add(f2)
        feeds.add(f3)
        return feeds
    }

}