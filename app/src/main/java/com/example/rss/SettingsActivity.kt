package com.example.rss

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rss.databinding.ActivitySettingsBinding
import org.jsoup.Jsoup

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("settings",Context.MODE_PRIVATE)
        binding.sSize.value = sharedPreference.getInt("size", 24).toFloat()
        binding.sCornerRadius.value = sharedPreference.getInt("cornerRadius", 0).toFloat()
        binding.cv.radius = binding.sCornerRadius.value

        val body = Jsoup.parse("<h1>Title</h1>")
        body.append("<div>Forbes mexico / Sasha grey</div>")
        body.append("<![CDATA[ <img src=\"https://i.kinja-img.com/gawker-media/image/upload/s--P_Jb0RA6--/c_fit,fl_progressive,q_80,w_636/56b98a9c3c7361755ea04dfeacdbf18f.jpg\" /><p>Source of over 2.9 percent of human disappointment, Logan Paul, may have spent \$3.5 million on a pile of fake Pokémon cards. After suspicions were raised by <a href=\"https://www.pokebeach.com/2021/12/logan-pauls-3-5-million-base-set-case-may-be-fake-pokemon-community-uncovers-significant-evidence\" target=\"_blank\" rel=\"noopener noreferrer\"><em>Pok</em>é<em>Beach</em></a>, the former YouTuber has <a href=\"https://twitter.com/LoganPaul/status/1478568426553561088\" target=\"_blank\" rel=\"noopener noreferrer\">announced</a> he’s off to Chicago to get the case of first edition base set boosters properly verified.<br></p><p><a href=\"https://kotaku.com/logan-paul-spends-3-5-million-on-pokemon-cards-that-ar-1848307244\">Read more...</a></p> ]]>")
        val head = body.head()
        head.append(
            """
                <style>
                    * {
                        text-align: justify;
                        line-height : 24px;
                        font-size: ${binding.sSize.value - 8}px;
                    }
                    h1 {
                        font-size: ${binding.sSize.value}px;
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
        binding.wvLorem.loadData(body.html(), "text/html", null)
        binding.sSize.addOnChangeListener { slider, value, fromUser ->
            val head = body.head()
            head.getElementsByTag("style").empty()
            head.append(
                """
                <style>
                    * {
                        text-align: justify;
                        line-height : 24px;
                        font-size: ${value.toInt() - 8}px;
                    }
                    h1 {
                        font-size: ${value.toInt()}px;
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
            binding.wvLorem.loadData(body.html(), "text/html", null)
            val sharedPreference = getSharedPreferences("settings",Context.MODE_PRIVATE)
            sharedPreference.edit().putInt("size", value.toInt()).commit()
            //Log.d("SHREEEEE", sharedPreference.getInt("size", 24).toString())

            //sharedPreference.edit().putInt("size", value.toInt()).commit()
        }

        binding.sCornerRadius.addOnChangeListener { slider, value, fromUser ->
            binding.cv.radius = value
            val sharedPreference = getSharedPreferences("settings",Context.MODE_PRIVATE)
            sharedPreference.edit().putInt("cornerRadius", value.toInt()).commit()
        }


    }
}