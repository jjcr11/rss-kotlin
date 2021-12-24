package com.example.rss

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rss.databinding.PostItemBinding
import org.jsoup.Jsoup

class PostAdapter(private var posts: MutableList<FeedEntity>): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = PostItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        with(holder) {
            var body = Jsoup.parse(post.content)
            var head = body.head()
            head.append(
                """
                <style>
                    img {
                        zoom: 10%
                    }
                </style>
                """.trimIndent()
            )
            binding.tvTitle.text = post.title
            binding.tvSource.text = post.sourceId.toString()
            binding.tvAuthor.text = post.author
            binding.wv.loadData(body.html(), "text/html", null)
        }

        /*val post = posts[position]
        with(holder) {
            binding.tv.text = post.title
        }*/
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}