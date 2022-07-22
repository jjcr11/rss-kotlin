package com.reader.rss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reader.rss.databinding.ActivitySourcesBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class SourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySourcesBinding
    private lateinit var sourceAdapter: SourceAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var viewOtherSourceBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cpi.visibility = View.GONE

        linearLayoutManager = LinearLayoutManager(this)

        //First initialize sourceAdapter with a empty mutable list
        sourceAdapter = SourceAdapter(mutableListOf())
        //After call getAllSources to get sources into the database
        getSources()

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = sourceAdapter
        }

        viewOtherSourceBar = layoutInflater.inflate(R.layout.other_source_bar, null)

        //Object transition of slide type from source_bar_transition.xml
        val transition: Transition = TransitionInflater
            .from(this)
            .inflateTransition(R.transition.source_bar_transition)

        //Scene to change the app bar layout in activity_source.xml to viewOtherSourceBar
        val fromAppBarLayoutToViewOtherSourceBar = Scene(binding.abl, viewOtherSourceBar)

        binding.mtb.menu.getItem(0).setOnMenuItemClickListener {
            TransitionManager.go(fromAppBarLayoutToViewOtherSourceBar, transition)
            true
        }

        viewOtherSourceBar.findViewById<MaterialToolbar>(R.id.mtb).menu.getItem(0).setOnMenuItemClickListener {
            downloadXmlTask(
                viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).text.toString())
            true
        }
    }

    //Function to get sources into the database
    private fun getSources() {
        CoroutineScope(Dispatchers.IO).launch {
            val sources = DatabaseApplication.database.dao().getAllSources()
            sourceAdapter.setSources(sources)
        }
    }

    private fun downloadXmlTask(url: String) {
        binding.cpi.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newSource = SourceEntity(
                    name = loadXmlFromNetwork(url),
                    url = viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).text.toString()
                )
                DatabaseApplication.database.dao().addSource(newSource)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.cpi.visibility = View.GONE
                    sourceAdapter.add(newSource)
                }
            } catch(e: MalformedURLException) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.cpi.visibility = View.GONE
                    Toast.makeText(baseContext, "Invalid url", Toast.LENGTH_SHORT).show()
                }
            } catch(e: XmlPullParserException) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.cpi.visibility = View.GONE
                    Toast.makeText(baseContext, "Rss feeds not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadXmlFromNetwork(urlString: String): String {
        var source: SourceEntity? = null
        downloadUrl(urlString)?.use { stream ->
            source = XmlParser().parse(stream)
        }
        return "${source?.name}"
    }

    private fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 15000
            connectTimeout = 20000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }
}