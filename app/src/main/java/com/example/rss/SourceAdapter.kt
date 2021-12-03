package com.example.rss

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.SourcesItemBinding

class SourceAdapter(private val sources: List<Source>): RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = SourcesItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.sources_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val source = sources.get(position)
        with(holder) {
            binding.tvName.text = source.name
            binding.tvUrl.text = source.url
        }
    }

    override fun getItemCount(): Int {
        return sources.size
    }

}