package com.example.rss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivitySourcesBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
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

        //Hide the action bar by default
        supportActionBar?.hide()

        binding.cpi.visibility = View.GONE

        linearLayoutManager = LinearLayoutManager(this)

        //First initialize sourceAdapter with a empty mutable list
        sourceAdapter = SourceAdapter(mutableListOf())
        //After call getSources to get sources into the database
        getSources()

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = sourceAdapter
        }

        //View from other_source_bat.xml to do the transition
        //val viewOtherSourceBar = layoutInflater.inflate(R.layout.other_source_bar, null)
        viewOtherSourceBar = layoutInflater.inflate(R.layout.other_source_bar, null)
        //View from the material tool bar in activity_source.xml
        val viewMaterialToolBar: View = binding.mtb

        //Object transition of slide type from source_bar_transition.xml
        val transition: Transition = TransitionInflater
            .from(this)
            .inflateTransition(R.transition.source_bar_transition)

        //Scene to change the app bar layout in activity_source.xml to viewOtherSourceBar
        val fromAppBarLayoutToViewOtherSourceBar = Scene(binding.abl, viewOtherSourceBar)
        //Scene to change the app bar layout in activity_source.xml to viewMaterialToolBar
        val fromAppBarLayoutToViewMaterialToolBar = Scene(binding.abl, viewMaterialToolBar)

        binding.mtb.menu.getItem(0).setOnMenuItemClickListener {
            TransitionManager.go(fromAppBarLayoutToViewOtherSourceBar, transition)
            true
        }

        viewOtherSourceBar.findViewById<MaterialToolbar>(R.id.mtb).menu.getItem(0).setOnMenuItemClickListener {
            TransitionManager.go(fromAppBarLayoutToViewMaterialToolBar, transition)
            viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).setText("")
            true
        }

        viewOtherSourceBar.findViewById<ImageButton>(R.id.imgb).setOnClickListener {
            downloadXmlTask(
                viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).text.toString()
            )
        }
    }

    //Function to get sources into the database
    private fun getSources() {
        runBlocking(Dispatchers.IO) {
            val sources = DatabaseApplication.database.dao().getSources()
            sourceAdapter.setSources(sources)
        }
    }

    private fun downloadXmlTask(url: String) {
        binding.cpi.visibility = View.VISIBLE
        GlobalScope.launch {
            val name = loadXmlFromNetwork(url)
            val new = SourceEntity(
                name = name,
                url = viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).text.toString()
            )
            runBlocking(Dispatchers.IO) {
                DatabaseApplication.database.dao().addSource(new)
            }
            runBlocking(Dispatchers.Main) {
                binding.cpi.visibility = View.GONE
                sourceAdapter.add(new)
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private suspend fun loadXmlFromNetwork(urlString: String): String {
        val sources = withContext(Dispatchers.IO) {
            downloadUrl(urlString)?.use { stream ->
                XmlParser().parse(stream)
            }
        }
        return sources?.name.toString()
    }

    @Throws(IOException::class)
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