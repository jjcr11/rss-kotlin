package com.reader.rss

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.reader.rss.databinding.ActivitySettingsBinding
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.File
import java.io.InputStreamReader

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var openIntent: Intent

    private var activityResultLaunch = registerForActivityResult(StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data!!
            val inputStream = contentResolver.openInputStream(uri)
            val inputStreamReader = InputStreamReader(inputStream)
            val sourceXFeedType = object : TypeToken<MutableList<SourceXFeed?>?>() {}.type

            val sourceXFeeds: List<SourceXFeed> = Gson().fromJson(inputStreamReader.readText(), sourceXFeedType)
            CoroutineScope(Dispatchers.IO).launch {
                val async = async {
                    for(source in sourceXFeeds) {

                        val s = SourceEntity(
                            source.id,
                            source.name,
                            source.url,
                            source.count
                        )
                        DatabaseApplication.database.dao().addSource(s)
                        for(feed in source.feeds) {
                            val f = FeedEntity(
                                feed.id,
                                feed.title,
                                feed.url,
                                feed.author,
                                feed.date,
                                feed.content,
                                feed.sourceId,
                                feed.read,
                                feed.saved
                            )
                            DatabaseApplication.database.dao().addFeed(f)
                        }
                }

                }
                async.await()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(baseContext, "Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        openIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    activityResultLaunch.launch(openIntent)
                }
            }

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
        binding.tvThemeSystem.text = when(sharedPreference.getInt("theme", 2)) {
            0 -> "Yes"
            1 -> "No"
            else -> "System"
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

        val background: String
        val text: String

        when(binding.tvThemeSystem.currentTextColor) {
            -14408668 -> {
                background = "rgb(255, 255, 255)"
                text = "rgb(36, 36, 36)"
            }
            else -> {
                background = "rgb(36, 36, 36)"
                text = "rgb(255, 255, 255)"
            }
        }

        sharedPreference.edit().putString("background", background).apply()



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

        binding.sSize.addOnChangeListener { _, value, _ ->
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

        binding.sLineHeight.addOnChangeListener { _, value, _ ->
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

        binding.mbtg.addOnButtonCheckedListener { _, checkedId, _ ->
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

        binding.cvTheme.setOnClickListener {
            var i = sharedPreference.getInt("theme", 2)
            val options = arrayOf("Yes", "No", "System")
            MaterialAlertDialogBuilder(this)
                .setTitle("Night theme")
                .setSingleChoiceItems(options, i) { _, index ->
                    i = index
                }
                .setPositiveButton("Accept") { _, _ ->
                    when(i) {
                        0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    binding.tvThemeSystem.text = options[i]
                    sharedPreference.edit().putInt("theme", i).apply()
                }
                .show()
        }

        binding.cvDelete.setOnClickListener {
            var i = sharedPreference.getInt("indexDays", 2)
            val time = arrayOf("After 1 day", "After 2 days", "After 5 days", "After 10 days",
                "After 15 days", "Never")
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete unread feeds")
                .setSingleChoiceItems(time, i) { _, index ->
                    i = index
                }
                .setPositiveButton("Accept") { _, _ ->
                    binding.tvDeleteAfter.text = time[i]
                    sharedPreference.edit().putInt("indexDays", i).apply()
                }
                .show()
        }

        binding.cvExportImport.setOnClickListener {
            val actions = arrayOf("Export", "Import")
            MaterialAlertDialogBuilder(this)
                .setItems(actions) { _, index ->
                    when(index) {
                        0 -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val auxList: MutableList<SourceXFeed> = mutableListOf()
                                val sources = DatabaseApplication.database.dao().getAllSources()
                                for(source in sources) {
                                    val aux = SourceXFeed(
                                        source.id,
                                        source.name!!,
                                        source.url!!,
                                        source.count,
                                    )
                                    val feeds = DatabaseApplication.database.dao().getAllFeedsById(source.id)
                                    for(feed in feeds) {
                                        aux.feeds.add(feed)
                                    }
                                    auxList.add(aux)
                                }
                                val content: String = Gson().toJson(auxList)
                                val file = File(baseContext.filesDir, "backup.json")

                                file.writeText(content)
                                val shareIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                                        baseContext,
                                        baseContext.applicationContext.packageName + ".provider", file))
                                    type = "text/json"
                                }
                                startActivity(Intent.createChooser(shareIntent, null))
                            }
                        }
                        1 -> {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        baseContext,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        activityResultLaunch.launch(openIntent)
                                        //Toast.makeText(baseContext, "Successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                    else -> {
                                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            } else {
                                activityResultLaunch.launch(openIntent)
                                //Toast.makeText(baseContext, "Successfully", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }

        binding.mbtnReset.setOnClickListener {
            binding.sSize.value = 24f
            binding.sLineHeight.value = 24f
            binding.sCornerRadius.value = 0f
            binding.tvDeleteAfter.text = "After 5 days"
            sharedPreference.edit().putInt("indexDays", 2).apply()
        }
    }
}