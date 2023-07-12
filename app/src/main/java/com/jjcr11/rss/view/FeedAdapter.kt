package com.jjcr11.rss.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.Feed
import com.jjcr11.rss.databinding.FeedItemBinding

class FeedAdapter(
    private val feeds: MutableList<Feed>,
    private val listener: FeedAdapterOnClick
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FeedItemBinding.bind(view)
        fun setListener(feeds: List<Feed>, position: Int) {
            binding.root.setOnClickListener {
                listener.onClick(feeds, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feeds[position]
        holder.binding.let {
            it.tvTitle.text = feed.title
            it.tvSource.text = feed.sourceId.toString()
            it.tvHour.text = feed.date.toString()
        }
        holder.setListener(feeds, position)
    }

    override fun getItemCount(): Int = feeds.size

    fun add(feed: Feed) {
        notifyItemInserted(itemCount)
        feeds.add(feed)
    }

    fun clear() {
        notifyItemRangeRemoved(0, itemCount)
        feeds.clear()
    }
}