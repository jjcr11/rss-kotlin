package com.example.rss

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.FeedItemBinding

//Adapter to be used by the cards from headline_item.xml
class FeedAdapter(
    private var feeds: MutableList<FullFeedEntity>,
    private var cornerRadius: Int,
    private val listener: FeedAdapterOnClickListener
    ): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = FeedItemBinding.bind(view)
        fun setListener(feed: FullFeedEntity, position: Int) {
            binding.root.setOnClickListener {
                listener.onClick(feed, position)
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
        with(holder) {
            setListener(feed, position)
            binding.tvTitle.text = feed.title
            binding.tvSource.text = feed.source
            binding.tvHour.text = feed.date.toString()
            binding.cv.radius = cornerRadius.toFloat()
        }
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    fun add(feedEntity: FullFeedEntity) {
        feeds.add(feedEntity)
        notifyDataSetChanged()
    }

    fun setFeeds(feeds: MutableList<FullFeedEntity>) {
        this.feeds = feeds
        notifyDataSetChanged()
    }

    fun getFeeds(): MutableList<FullFeedEntity> {
        return feeds
    }
}