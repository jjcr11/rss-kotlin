package com.reader.rss

import android.content.Context
import android.util.Log
import android.view.FrameMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.reader.rss.databinding.PostItemBinding
import org.jsoup.Jsoup

class PostAdapter(
    private var posts: MutableList<FullFeedEntity>,
    private var size: Int,
    theme: Boolean,
    private var lineHeight: Int,
    private var align: String
): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val r = Regex("""style *= *".*"""")
    private val r2 = Regex("#")
    private val blue = if(theme) { "rgb(12, 75, 87)" } else { "rgb(119, 216, 236)" }
    private val background = if(theme) { "rgb(36, 36, 36)" } else { "rgb(255, 255, 255)" }
    private val text = if(theme) { "rgb(255, 255, 255)" } else { "rgb(36, 36, 36)" }

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
        val body = Jsoup.parse("<h1>${post.title}</h1>")
        if(post.author == null) {
            body.append("<div class=\"source\">${post.source} / Unknown</div>")
        } else {
            body.append("<div class=\"source\">${post.source} / ${post.author}</div>")
        }
        val post2 = post.content.replace(r, "")
        val post3 = post2.replace(r2, "")
        body.append(post3)
        val head = body.head()
        head.append(
            """
                <style>
                    * {
                        text-align: ${align};
                        line-height: ${lineHeight}px;
                        font-size: ${size - 8};
                        background-color: ${background};
                        color: ${text};
                    }
                    .source {
                        font-style: italic;
                    }
                    h1 {
                        font-size: ${size}px;
                    }
                    a:link {
                        color: ${blue};
                    }
                    img {
                        width: 100%;
                        height: auto;
                    }
                    video {
                        width: 100%;
                        height: auto;
                    }
                    iframe {
                        width: 100%;
                        height: auto;
                    }
                </style>
                """.trimIndent()
        )
        with(holder) {
            binding.wv.settings.javaScriptEnabled = true
            binding.wv.loadData(body.html(), "text/html", null)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}