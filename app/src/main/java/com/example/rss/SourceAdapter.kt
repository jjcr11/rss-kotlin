package com.example.rss

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.SourcesItemBinding

//Adapter to be used by the cards from sources_item.xml
class SourceAdapter(private var sources: MutableList<SourceEntity>): RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

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
        val source = sources[position]
        with(holder) {
            binding.tvName.text = source.name
            binding.tvUrl.text = source.url
            binding.imgDelete.setOnClickListener {
                val t = Thread {
                    DatabaseApplication.database.dao().deleteSourceById(source.id)
                }
                t.start()
                t.join()
                sources.remove(source)
                notifyDataSetChanged()
                Toast.makeText(context, "You can see the feeds from ${source.name} until close the app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    fun add(sourceEntity: SourceEntity) {
        sources.add(sourceEntity)
        //This show the added sourceEntity in the view immediately
        notifyDataSetChanged()
    }

    fun setSources(sources: MutableList<SourceEntity>) {
        this.sources = sources
        //This show the added sourceEntity in the view immediately
        notifyDataSetChanged()

    }
}