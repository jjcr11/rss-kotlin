package com.example.rss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivitySourcesBinding

class SourcesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySourcesBinding
    private lateinit var sourceAdapter: SourceAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        sourceAdapter = SourceAdapter(getSources())
        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = sourceAdapter
        }

    }

    private fun getSources(): MutableList<Source> {
        val sources = mutableListOf<Source>()
        val s1 = Source("Forbes Mexico", "forbesmexico.com/feed")
        val s2 = Source("Mientras tanto en Mexico", "mientrastantoenmexico.com/feed")
        val s3 = Source("Xataka Mexico", "xatakamexico.com/feed")
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        return sources
    }

}