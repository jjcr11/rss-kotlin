package com.jjcr11.rss.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jjcr11.rss.data.model.Feed

class FeedItemAdapter(
    fragmentAdapter: FeedFragment,
    private val feeds: List<Feed>
) : FragmentStateAdapter(fragmentAdapter) {

    override fun getItemCount(): Int = feeds.size

    override fun createFragment(position: Int): Fragment {
        val feed = feeds[position]
        return FeedItemFragment(feed)
    }
}