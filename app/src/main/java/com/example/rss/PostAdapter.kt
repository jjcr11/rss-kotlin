package com.example.rss

import android.content.Context
import android.util.Log
import android.view.FrameMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rss.databinding.PostItemBinding
import org.jsoup.Jsoup

class PostAdapter(
    private var posts: MutableList<FeedEntity>,
    private val metrics: Map<String, Int>,
    private var size: Int
): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val height = metrics["height"]
    private val width = metrics["width"]

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
        val r = Regex("style *= *\".*\"")
        with(holder) {
            val body = Jsoup.parse("<h1>${post.title}</h1>")
            body.append("<div>${post.sourceId} / ${post.author}</div>")
            val post2 = post.content.replace(r, "")
            body.append(post2)
            val head = body.head()
            head.append(
                """
                <style>
                    * {
                        text-align: justify;
                        line-height: 24px;
                        font-size: ${size - 8};
                    }
                    h1 {
                        font-size: ${size}px;
                    }
                    a:link {
                        color: rgb(119, 216, 236);
                    }
                    img {
                        zoom: 25%;
                    }
                </style>
                """.trimIndent()
            )
            binding.wv.settings.javaScriptEnabled = true
            binding.wv.loadData(body.html(), "text/html", null)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}