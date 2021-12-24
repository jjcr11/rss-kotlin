package com.example.rss

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.HeadlineItemBinding

//Adapter to be used by the cards from headline_item.xml
class FeedAdapter(
    private var feeds: MutableList<FeedEntity>,
    private var sources: MutableList<String>,
    private val listener: FeedAdapterOnClickListener
    ): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = HeadlineItemBinding.bind(view)
        fun setListener(feed: FeedEntity, position: Int) {
            binding.root.setOnClickListener {
                listener.onClick(feed, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.headline_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feeds[position]
        val source = sources[position]
        with(holder) {
            setListener(feed, position)
            binding.tvTitle.text = feed.title
            binding.tvSource.text = source
            binding.tvHour.text = feed.date.toString()
        }
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    fun add(feedEntity: FeedEntity) {
        feeds.add(feedEntity)
        notifyDataSetChanged()
    }

    fun setFeeds(feeds: MutableList<FeedEntity>) {
        this.feeds = feeds
        notifyDataSetChanged()
    }

    fun getFeeds(): MutableList<FeedEntity> {
        return feeds
    }

    fun setSources(sources: MutableList<String>) {
        this.sources = sources
        notifyDataSetChanged()
    }
}