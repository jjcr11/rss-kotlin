package com.jjcr11.rss.view

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.Feed
import com.jjcr11.rss.databinding.FragmentFeedItemBinding
import org.jsoup.Jsoup

class FeedItemFragment(
    private val feed: Feed
) : Fragment() {

    private lateinit var mBinding: FragmentFeedItemBinding
    //private val r = Regex("""style *= *".*"""")
    //private val r2 = Regex("#")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFeedItemBinding.inflate(inflater, container, false)

        //mBinding.tv.text = Html.fromHtml(feed.description).toString()

        //Html.ImageGetter("")

        //feed.description.replace(r, "")

        val document = Jsoup.parse(feed.description)
        document.allElements.forEach {
            //Log.d("", "${it.tag()}")
            when (it.tag().toString()) {
                "img" -> {
                    val u = it.attr("src")
                    val t = ImageView(requireContext())
                    Glide.with(this)
                        .load(u)
                        .into(t)
                    mBinding.ll.addView(t)
                }
                "p" -> {
                    val t = TextView(requireContext())
                    t.text = it.text()
                    mBinding.ll.addView(t)
                }
            }
        }
        /*val b = a.getElementsByTag("img")
        b.forEach {
            //Log.d("", "${it.attr("src")}")
            val u = it.attr("src")
            val t = ImageView(requireContext())
            Glide.with(this)
                .load(u)
                .into(t)
            mBinding.ll.addView(t)
        }*/

        val s = """
                <style>
                    * {
                        text-align: justify;
                        font-size: ${20};
                    }
                    .source {
                        font-style: italic;
                    }
                    h1 {
                        font-size: ${8}px;
                    }
                    a:link {
                        
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

        //mBinding.wv.settings.javaScriptEnabled = true

        //mBinding.wv.loadData("$s${feed.description}", "text/html", null)

        return mBinding.root
    }
}