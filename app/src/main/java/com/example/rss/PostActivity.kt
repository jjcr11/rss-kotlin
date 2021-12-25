package com.example.rss

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rss.databinding.ActivityPostBinding
import com.google.android.material.button.MaterialButton
import java.util.zip.Inflater

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*var v = layoutInflater.inflate(R.layout.post_item, null)
        val v2 = v.findViewById<MaterialButton>(R.id.btnShare)
        v2.text = "FSFDSFDS"
        Log.d("BUTTON", v2.text.toString())*/


        supportActionBar?.hide()

        val list: MutableList<FeedEntity> = intent.getSerializableExtra("list") as MutableList<FeedEntity>
        val position: Int = intent.getIntExtra("position", 0)

        postAdapter = PostAdapter(list)

        binding.vp.apply {
            adapter = postAdapter
            currentItem = position
            reduceDragSensitivity()
        }

        binding.vp.setPageTransformer { page, position ->
            Thread {
                DatabaseApplication.database.dao().setRead(list[binding.vp.currentItem].id, true)
            }.start()
            //Log.d("FEED", list[binding.vp.currentItem].id.toString())
        }


        //binding.vp.currentItem = position

        //binding.vp.reduceDragSensitivity()




    }
}

private fun ViewPager2.reduceDragSensitivity(f: Int = 4) {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop*f)       // "8" was obtained experimentally
}
