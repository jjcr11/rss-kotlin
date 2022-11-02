package com.reader.rss

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reader.rss.databinding.ActivityPostBinding
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.*

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter
    private lateinit var list: MutableList<FullFeedEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postAdapter = PostAdapter(this, mutableListOf())

        binding.vp.apply {
            adapter = postAdapter
            reduceDragSensitivity()
        }

        getFeeds()

        binding.vp.setPageTransformer { _, _ ->
            lifecycleScope.launch {
                var url: String
                var saved: Boolean
                withContext(Dispatchers.IO) {
                    DatabaseApplication.database.dao().setFeedAsRead(list[binding.vp.currentItem].id)
                    saved = DatabaseApplication.database.dao().getFeedSaved(list[binding.vp.currentItem].id)
                    url = DatabaseApplication.database.dao().getFeedUrl(list[binding.vp.currentItem].id)
                }
                saveButtonSetup(saved)
                binding.mtb.menu.getItem(1).let {
                    it.setOnMenuItemClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        true
                    }
                }
                binding.mtb.menu.getItem(2).let {
                    it.setOnMenuItemClickListener {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(sendIntent, null))
                        true
                    }
                }
            }
        }
    }

    private fun saveButtonSetup(saved: Boolean) {
        binding.mtb.menu.getItem(0).let {
            it.isChecked = saved
            it.icon = if (saved) {
                ContextCompat.getDrawable(baseContext, R.drawable.ic_bookmark)
            } else {
                ContextCompat.getDrawable(baseContext, R.drawable.ic_bookmark_border)
            }
            it.setOnMenuItemClickListener {
                lifecycleScope.launch {
                    it.isChecked = !it.isChecked
                    withContext(Dispatchers.IO) {
                        DatabaseApplication.database.dao().setFeedAsSavedOrUnsaved(
                            list[binding.vp.currentItem].id,
                            it.isChecked
                        )
                    }
                    it.icon = if (binding.mtb.menu.getItem(0).isChecked) {
                        ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.ic_bookmark
                        )
                    } else {
                        ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.ic_bookmark_border
                        )
                    }
                }
                true
            }
        }
    }

    private fun getFeeds() {
        val position: Int = intent.getIntExtra("position", 0)
        val saved: Boolean = intent.getBooleanExtra("saved", false)
        val sort: Boolean = intent.getBooleanExtra("sort", false)
        CoroutineScope(Dispatchers.IO).launch {
            val asyncJob = async {
                list = if(saved) {
                    DatabaseApplication.database.dao().getAllFeedsSaved()
                } else if(sort) {
                    DatabaseApplication.database.dao().getUnreadFeeds()
                } else {
                    DatabaseApplication.database.dao().getUnreadFeedsDesc()
                }
            }
            asyncJob.await()
            CoroutineScope(Dispatchers.Main).launch {
                postAdapter.setList(list)
                binding.vp.currentItem = position
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