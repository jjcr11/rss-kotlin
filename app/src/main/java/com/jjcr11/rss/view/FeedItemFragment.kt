package com.jjcr11.rss.view

import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.Feed
import com.jjcr11.rss.databinding.FragmentFeedItemBinding
import org.jsoup.Jsoup

class FeedItemFragment(
    private val feed: Feed
) : Fragment() {

    private lateinit var mBinding: FragmentFeedItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFeedItemBinding.inflate(inflater, container, false)

        val density = resources.displayMetrics.density
        val document = Jsoup.parse(feed.description)

        var textView = TextView(requireContext()).also {
            it.text = feed.title
            it.textSize = 8 * density
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                (5 * density).toInt(),
                (7 * density).toInt(),
                (5 * density).toInt(),
                (7 * density).toInt()
            )
            it.layoutParams = layoutParams
            it.setTextColor(resources.getColor(R.color.black, null))
            it.setTypeface(it.typeface, Typeface.BOLD)
        }
        mBinding.ll.addView(textView)

        document.allElements.forEach {
            when (it.tag().toString()) {
                "img" -> {
                    val url = it.attr("src")
                    val imageView = ImageView(requireContext()).also { iv ->
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins((5 * density).toInt())
                        iv.layoutParams = layoutParams
                    }
                    Glide.with(this)
                        .load(url)
                        .into(imageView)
                    mBinding.ll.addView(imageView)
                }

                "p" -> {
                    textView = TextView(requireContext()).also { tv ->
                        val a = it.getElementsByTag("a")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            tv.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }

                        tv.textSize = 7 * density
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(
                            (5 * density).toInt(),
                            (7 * density).toInt(),
                            (5 * density).toInt(),
                            0
                        )
                        tv.layoutParams = layoutParams
                        if (a.isNotEmpty()) {
                            tv.isClickable = true
                            tv.movementMethod = LinkMovementMethod.getInstance()
                            tv.text = Html.fromHtml(a.toString())
                            tv.setTextColor(resources.getColor(R.color.blue_light, null))
                        } else {
                            tv.text = it.text()
                            tv.setTextColor(resources.getColor(R.color.black, null))
                        }

                    }
                    mBinding.ll.addView(textView)
                }
            }
        }

        return mBinding.root
    }
}