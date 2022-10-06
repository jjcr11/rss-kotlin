package com.reader.rss

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reader.rss.databinding.ActivitySettingsBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jsoup.Jsoup

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val sharedPreference = getSharedPreferences("settings",Context.MODE_PRIVATE)
        binding.sSize.value = sharedPreference.getInt("size", 24).toFloat()
        binding.sLineHeight.value = sharedPreference.getInt("lineHeight", 24).toFloat()
        binding.sCornerRadius.value = sharedPreference.getInt("cornerRadius", 0).toFloat()
        binding.tvDeleteAfter.text = when(sharedPreference.getInt("indexDays", 2)) {
            0 -> "After 1 day"
            1 -> "After 2 days"
            2 -> "After 5 days"
            3 -> "After 10 days"
            4 -> "After 15 days"
            else -> "Never"
        }


        binding.tvValueSize.text = binding.sSize.value.toInt().toString()
        binding.tvValueLineHeight.text = binding.sLineHeight.value.toInt().toString()
        binding.tvValueCornerRadius.text = binding.sCornerRadius.value.toInt().toString()

        binding.cv.radius = binding.sCornerRadius.value



        var align = sharedPreference.getString("align", "Left")
        when (align) {
            binding.mbtnLeft.text.toString() -> {
                binding.mbtg.check(R.id.mbtnLeft)
            }
            binding.mbtnCenter.text.toString() -> {
                binding.mbtg.check(R.id.mbtnCenter)
            }
            binding.mbtnRight.text.toString() -> {
                binding.mbtg.check(R.id.mbtnRight)
            }
            else -> {
                binding.mbtg.check(R.id.mbtnJustify)
            }
        }

        binding.sm.isChecked = sharedPreference.getBoolean("theme", false)
        val background = if(binding.sm.isChecked) { "rgb(36, 36, 36)" } else { "rgb(255, 255, 255)" }
        val text = if(binding.sm.isChecked) { "rgb(255, 255, 255)" } else { "rgb(36, 36, 36)" }

        val body = Jsoup.parse("<h1>Title</h1>")
        body.append("<div class=\"source\">Source / Unknown</div>")
        body.append(getString(R.string.card_view_content))
        val head = body.head()
        head.append(
            """
                <style>
                    * {
                        text-align: ${align};
                        line-height : ${binding.sLineHeight.value}px;
                        font-size: ${binding.sSize.value - 8}px;
                        background-color: ${background};
                        color: ${text};
                    }
                    .source {
                        font-style: italic;
                    }
                    h1 {
                        font-size: ${binding.sSize.value}px;
                    }
                </style>
                """.trimIndent()
        )
        binding.wvLorem.loadData(body.html(), "text/html", null)

        binding.sSize.addOnChangeListener { slider, value, fromUser ->
            binding.tvValueSize.text = value.toInt().toString()
            head.getElementsByTag("style").remove()
            head.append(
                """
                <style>
                    * {
                        text-align: ${align};
                        line-height : ${binding.sLineHeight.value}px;
                        font-size: ${value - 8}px;
                        background-color: ${background};
                        color: ${text};
                    }
                    .source {
                        font-style: italic;
                    }
                    h1 {
                        font-size: ${value}px;
                    }
                </style>
                """.trimIndent()
            )
            binding.wvLorem.loadData(body.html(), "text/html", null)
            sharedPreference.edit().putInt("size", value.toInt()).apply()
        }

        binding.sLineHeight.addOnChangeListener { slider, value, fromUser ->
            binding.tvValueLineHeight.text = value.toInt().toString()
            head.getElementsByTag("style").remove()
            head.append(
                """
                <style>
                    * {
                        text-align: ${align};
                        line-height : ${value}px;
                        font-size: ${binding.sSize.value - 8}px;
                        background-color: ${background};
                        color: ${text};
                    }
                    .source {
                        font-style: italic;
                    }
                    h1 {
                        font-size: ${binding.sSize.value}px;
                    }
                </style>
                """.trimIndent()
            )
            binding.wvLorem.loadData(body.html(), "text/html", null)
            sharedPreference.edit().putInt("lineHeight", value.toInt()).apply()
        }

        binding.mbtg.addOnButtonCheckedListener { group, checkedId, isChecked ->
            align = findViewById<MaterialButton>(checkedId).text.toString()
            head.getElementsByTag("style").remove()
            head.append(
                """
                <style>
                    * {
                        text-align: ${findViewById<MaterialButton>(checkedId).text};
                        line-height : ${binding.sLineHeight.value}px;
                        font-size: ${binding.sSize.value - 8}px;
                        background-color: ${background};
                        color: ${text};
                    }
                    .source {
                        font-style: italic;
                    }
                    h1 {
                        font-size: ${binding.sSize.value}px;
                    }
                </style>
                """.trimIndent()
            )
            binding.wvLorem.loadData(body.html(), "text/html", null)
            sharedPreference.edit().putString("align", findViewById<MaterialButton>(checkedId).text.toString()).apply()
        }

        binding.sCornerRadius.addOnChangeListener { _, value, _ ->
            binding.tvValueCornerRadius.text = value.toInt().toString()
            binding.cv.radius = value
            sharedPreference.edit().putInt("cornerRadius", value.toInt()).apply()
        }

        binding.sm.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedPreference.edit().putBoolean("theme", isChecked).apply()
        }

        binding.cvDelete.setOnClickListener {
            val fruits = arrayOf("After 1 day", "After 2 days", "After 5 days", "After 10 days",
                "After 15 days", "Never")
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete unread feeds")
                .setSingleChoiceItems(fruits, sharedPreference.getInt("indexDays", 2)) { _, index ->
                    binding.tvDeleteAfter.text = fruits[index]
                    sharedPreference.edit().putInt("indexDays", index).apply()
                }
                .setPositiveButton("Accept") { _, _ ->

                }
                .show()
        }

        binding.mbtnReset.setOnClickListener {
            binding.sSize.value = 24f
            binding.sLineHeight.value = 24f
            binding.sCornerRadius.value = 0f
            binding.sm.isChecked = false
            binding.tvDeleteAfter.text = "After 5 days"
            sharedPreference.edit().putInt("indexDays", 2).apply()
        }
    }
}