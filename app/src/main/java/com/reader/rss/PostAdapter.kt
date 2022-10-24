package com.reader.rss

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PostAdapter(
    private val fragmentActivity: FragmentActivity,
    private var list: List<FullFeedEntity>
): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        val sharedPreference = fragmentActivity.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return PostFragment(
            list[position],
            sharedPreference.getInt("size", 24),
            fragmentActivity.intent.getIntExtra("theme", -14408668),
            sharedPreference.getInt("lineHeight", 24),
            sharedPreference.getString("align", "Left")!!
        )
    }

    fun setList(list: List<FullFeedEntity>) {
        this.list = list
        notifyDataSetChanged()
    }
}