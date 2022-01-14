package com.example.rss

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Icon
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get


class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mtb

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val metrics = mapOf("height" to displayMetrics.heightPixels, "width" to displayMetrics.widthPixels)

        supportActionBar?.hide()

        val list: MutableList<FullFeedEntity> = intent.getSerializableExtra("list") as MutableList<FullFeedEntity>
        val position: Int = intent.getIntExtra("position", 0)

        val sharedPreference = getSharedPreferences("settings",Context.MODE_PRIVATE)
        postAdapter = PostAdapter(list, metrics, sharedPreference.getInt("size", 24))

        binding.vp.apply {
            adapter = postAdapter
            currentItem = position
            reduceDragSensitivity()
        }

        binding.vp.setPageTransformer { page, position ->
            var url = ""
            var saved = true
            val t1 = Thread {
                DatabaseApplication.database.dao().setRead(list[binding.vp.currentItem].id, true)
                saved = DatabaseApplication.database.dao().getSaved(list[binding.vp.currentItem].id)
            }
            t1.start()
            t1.join()
            binding.mtb.menu.getItem(0).isChecked = saved
            if(saved) {
                binding.mtb.menu.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark)
            } else {
                binding.mtb.menu.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border)
            }
            binding.mtb.menu.getItem(2).setOnMenuItemClickListener {
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
            binding.mtb.menu.getItem(1).setOnMenuItemClickListener {
                val t2 = Thread {
                    url = DatabaseApplication.database.dao().getFeedURL(list[binding.vp.currentItem].id)
                }
                t2.start()
                t2.join()
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("$url"))
                startActivity(browserIntent)
                true
            }

            binding.mtb.menu.getItem(0).setOnMenuItemClickListener {
                binding.mtb.menu.getItem(0).isChecked = !binding.mtb.menu.getItem(0).isChecked
                val t2 = Thread {
                    DatabaseApplication.database.dao().setSaved(list[binding.vp.currentItem].id, binding.mtb.menu.getItem(0).isChecked)
                }
                t2.start()
                t2.join()
                if(binding.mtb.menu.getItem(0).isChecked) {
                    binding.mtb.menu.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark)
                } else {
                    binding.mtb.menu.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border)
                }
                Log.d("SAVED", binding.mtb.menu.getItem(0).isChecked.toString())
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
