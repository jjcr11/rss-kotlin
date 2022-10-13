package com.reader.rss

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.reader.rss.databinding.ActivityPostBinding
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter
    private lateinit var list: MutableList<FullFeedEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)

        postAdapter = PostAdapter(
            mutableListOf(),
            sharedPreference.getInt("size", 24),
            intent.getIntExtra("theme", -14408668),
            sharedPreference.getInt("lineHeight", 24),
            sharedPreference.getString("align", "Left")!!
        )

        binding.vp.apply {
            adapter = postAdapter
            reduceDragSensitivity()
        }

        getFeeds()

        binding.vp.setPageTransformer { _, _ ->
            var url = ""
            var saved = false

            CoroutineScope(Dispatchers.IO).launch {
                val async1 = async {
                    DatabaseApplication.database.dao().setFeedAsRead(list[binding.vp.currentItem].id)
                    saved = DatabaseApplication.database.dao().getFeedSaved(list[binding.vp.currentItem].id)
                    url = DatabaseApplication.database.dao().getFeedUrl(list[binding.vp.currentItem].id)
                }
                async1.await()
                binding.mtb.menu.apply {
                    getItem(0).apply {
                        CoroutineScope(Dispatchers.Main).launch {
                            isChecked = saved
                            icon = if (saved) {
                                ContextCompat.getDrawable(baseContext, R.drawable.ic_bookmark)
                            } else {
                                ContextCompat.getDrawable(baseContext, R.drawable.ic_bookmark_border)
                            }
                        }
                        setOnMenuItemClickListener {
                            this.isChecked = !this.isChecked
                            CoroutineScope(Dispatchers.IO).launch {
                                val async2 = async {
                                    DatabaseApplication.database.dao().setFeedAsSavedOrUnsaved(
                                        list[binding.vp.currentItem].id,
                                        binding.mtb.menu.getItem(0).isChecked
                                    )
                                }
                                async2.await()
                                CoroutineScope(Dispatchers.Main).launch {
                                    binding.mtb.menu.getItem(0).icon =
                                        if (binding.mtb.menu.getItem(0).isChecked) {
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
                            }
                            true
                        }
                    }
                    getItem(1).apply {
                        setOnMenuItemClickListener {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            true
                        }
                    }
                    getItem(2).apply {
                        setOnMenuItemClickListener {
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
                postAdapter.setPosts(list)
                binding.vp.currentItem = position
                delay(1000)
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