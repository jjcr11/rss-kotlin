package com.example.rss

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rss.databinding.ActivityPostBinding
import com.google.android.material.button.MaterialButton
import java.util.zip.Inflater
import android.util.DisplayMetrics




class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val metrics = mapOf("height" to displayMetrics.heightPixels, "width" to displayMetrics.widthPixels)

        supportActionBar?.hide()

        val list: MutableList<FeedEntity> = intent.getSerializableExtra("list") as MutableList<FeedEntity>
        val position: Int = intent.getIntExtra("position", 0)

        postAdapter = PostAdapter(list, metrics)

        binding.vp.apply {
            adapter = postAdapter
            currentItem = position
            reduceDragSensitivity()
        }

        binding.vp.setPageTransformer { page, position ->
            var url = ""
            val t1 = Thread {
                DatabaseApplication.database.dao().setRead(list[binding.vp.currentItem].id, true)

            }
            t1.start()
            t1.join()
            binding.mtb.menu.getItem(1).setOnMenuItemClickListener {
                val t2 = Thread {
                    url = DatabaseApplication.database.dao().getFeedURL(list[binding.vp.currentItem].id)
                }
                t2.start()
                t2.join()
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$url")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                true
            }
            binding.mtb.menu.getItem(0).setOnMenuItemClickListener {
                val t2 = Thread {
                    url = DatabaseApplication.database.dao().getFeedURL(list[binding.vp.currentItem].id)
                }
                t2.start()
                t2.join()
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("$url"))
                startActivity(browserIntent)
                true
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------------------
//Code from https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
//-------------------------------------------------------------------------------------------------------------
private fun ViewPager2.reduceDragSensitivity(f: Int = 4) {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop*f)
}
