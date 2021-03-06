package com.reader.rss

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reader.rss.databinding.ActivitySourcesBinding
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
                viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).text.toString(),
                this
            )
            true
        }
    }

    //Function to get sources into the database
    private fun getSources() {
        runBlocking(Dispatchers.IO) {
            val sources = com.reader.rss.DatabaseApplication.database.dao().getAllSources()
            sourceAdapter.setSources(sources)
        }
    }

    private fun downloadXmlTask(url: String, context: Context) {
        binding.cpi.visibility = View.VISIBLE

            GlobalScope.launch {
                try {
                    val name = loadXmlFromNetwork(url)
                    val new = SourceEntity(
                        name = name,
                        url = viewOtherSourceBar.findViewById<TextInputEditText>(R.id.tiBar).text.toString()
                    )
                    runBlocking(Dispatchers.IO) {
                        com.reader.rss.DatabaseApplication.database.dao().addSource(new)
                    }
                    runBlocking(Dispatchers.Main) {
                        binding.cpi.visibility = View.GONE
                        sourceAdapter.add(new)
                    }
                }  catch (e: Exception) {
                    Log.d("EXCEPTION", e.toString())
                    runBlocking(Dispatchers.Main) {
                        binding.cpi.visibility = View.GONE
                        val dialog = AlertDialog.Builder(context)
                            .setMessage("Error with the link")
                            .setPositiveButton(
                                "ACCEPT",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // FIRE ZE MISSILES!
                                }
                            )
                            .create()
                        dialog.setOnShowListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black))
                            }
                        }
                        dialog.show()
                    }
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