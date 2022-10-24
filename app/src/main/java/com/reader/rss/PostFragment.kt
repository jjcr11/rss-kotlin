package com.reader.rss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reader.rss.databinding.FragmentPostBinding
import org.jsoup.Jsoup

class PostFragment(
    private val post: FullFeedEntity,
    private var size: Int,
    theme: Int,
    private var lineHeight: Int,
    private var align: String
): Fragment() {

    private lateinit var binding: FragmentPostBinding
    private val r = Regex("""style *= *".*"""")
    private val r2 = Regex("#")
    private val blue = if(theme != -14408668) { "rgb(12, 75, 87)" } else { "rgb(119, 216, 236)" }
    private val background = if(theme != -14408668) { "rgb(36, 36, 36)" } else { "rgb(255, 255, 255)" }
    private val text = if(theme != -14408668) { "rgb(255, 255, 255)" } else { "rgb(36, 36, 36)" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)

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

        binding.wv.settings.javaScriptEnabled = true
        binding.wv.loadData(body.html(), "text/html", null)


        return binding.root
    }
}