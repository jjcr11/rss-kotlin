package com.example.rss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list: MutableList<FeedEntity> = intent.getSerializableExtra("list") as MutableList<FeedEntity>
        val position: Int = intent.getIntExtra("position", 0)

        postAdapter = PostAdapter(list)

        binding.vp.apply {
            adapter = postAdapter
        }

        binding.vp.currentItem = position
    }
}