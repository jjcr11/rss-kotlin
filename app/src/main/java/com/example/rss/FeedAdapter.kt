package com.example.rss

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.HeadlineItemBinding

//Adapter to be used by the cards from headline_item.xml
class FeedAdapter(private var feeds: List<FeedEntity>): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = HeadlineItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.headline_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Thread {
            val feed = feeds.get(position)
            with(holder) {
                binding.tvTitle.text = feed.title
                binding.tvSource.text =
                    DatabaseApplication.database.sourceDao().getSourceNameByID(feed.sourceId)
                binding.tvHour.text = feed.date.toString()

            }
        }.start()
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    fun setFeeds(feeds: List<FeedEntity>) {
        this.feeds = feeds
        notifyDataSetChanged()
    }

}