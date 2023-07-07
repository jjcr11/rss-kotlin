package com.jjcr11.rss.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.Source
import com.jjcr11.rss.databinding.SourcesItemBinding

class SourceAdapter(
    private val sources: MutableList<Source>
) : RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = SourcesItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.sources_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val source = sources[position]
        holder.binding.let {
            it.tvName.text = source.title
            it.tvUrl.text = source.link
        }
    }

    override fun getItemCount(): Int = sources.size

    fun add(source: Source) {
        notifyItemInserted(itemCount)
        sources.add(source)
    }
}