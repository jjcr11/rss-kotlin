package com.jjcr11.rss.view

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.AppDatabase
import com.jjcr11.rss.data.model.Feed
import com.jjcr11.rss.data.model.Source
import com.jjcr11.rss.data.network.APIService
import com.jjcr11.rss.databinding.FragmentListSourceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.text.SimpleDateFormat

class ListSourceFragment : Fragment() {

    private lateinit var mBinding: FragmentListSourceBinding
    private lateinit var mAdapter: SourceAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val sources = AppDatabase.getDatabase(requireContext()).dao().getSources()
            mAdapter = SourceAdapter(sources as MutableList<Source>)
            //val sources2 = AppDatabase.getDatabase(requireContext()).dao().getSourcesWithFeeds()
            //Log.d("", "$sources2")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentListSourceBinding.inflate(inflater, container, false)

        mLayoutManager = LinearLayoutManager(requireContext())

        mBinding.rv.let {
            it.adapter = mAdapter
            it.layoutManager = mLayoutManager
        }

        mBinding.ib.setOnClickListener {
            val transform = MaterialContainerTransform().also {
                it.startView = mBinding.ib
                it.endView = mBinding.mtbText
                it.addTarget(mBinding.mtbText)
                it.scrimColor = Color.TRANSPARENT
            }
            TransitionManager.beginDelayedTransition(mBinding.abl, transform)
            mBinding.mtb.visibility = View.GONE
            mBinding.mtbText.visibility = View.VISIBLE
        }

        mBinding.mtbText.menu[0].setOnMenuItemClickListener {
            val text = mBinding.tiBar.text.toString()
            val index = text.indexOfLast { it == '/' }
            val retrofit = Retrofit.Builder()
                .baseUrl(text.take(index + 1))
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
            val api = retrofit.create(APIService::class.java)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    addRssContent(api, text, index)
                } catch (e: Exception) {
                    addRss(api, text, index)
                }?.let {
                    launch(Dispatchers.Main) {
                        mAdapter.add(it)
                    }
                }
            }
            true
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mBinding.mtb.visibility == View.GONE) {
                        val transform = MaterialContainerTransform().also {
                            it.startView = mBinding.mtbText
                            it.endView = mBinding.ib
                            it.addTarget(mBinding.ib)
                            it.scrimColor = Color.TRANSPARENT
                        }
                        TransitionManager.beginDelayedTransition(mBinding.abl, transform)
                        mBinding.mtbText.visibility = View.GONE
                        mBinding.mtb.visibility = View.VISIBLE
                    } else {
                        val navHostFragment =
                            requireActivity().supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
                        val navController = navHostFragment.navController
                        navController.popBackStack()
                    }

                }
            })

        return mBinding.root
    }

    private suspend fun addRssContent(api: APIService, text: String, index: Int): Source? {
        val call = api.getChannelContent(text.takeLast(text.length - index - 1))
        return call.body()?.let {
            val source = Source(0, it.title!!, text)
            val id = addSource(source)
            it.items?.forEach { item ->
                addFeed(id, item.title!!, item.link!!, item.content!!, item.date!!)
            }
            source
        }
    }

    private suspend fun addRss(api: APIService, text: String, index: Int): Source? {
        val call = api.getChannel(text.takeLast(text.length - index - 1))
        return call.body()?.let {
            val source = Source(0, it.title!!, text)
            val id = addSource(source)
            it.items?.forEach { item ->
                addFeed(id, item.title!!, item.link!!, item.description!!, item.date!!)
            }
            source
        }
    }

    private fun addSource(source: Source): Long {
        AppDatabase.getDatabase(requireContext()).dao().addSource(source)
        return AppDatabase.getDatabase(requireContext()).dao().getLastSourceId()
    }

    private fun addFeed(id: Long, title: String, link: String, content: String, date: String) {
        val nDate = SimpleDateFormat("E, dd LLL yyyy HH:mm:ss Z").parse(date)
        val feed = Feed(0, id, title, link, content, nDate)
        AppDatabase.getDatabase(requireContext()).dao().addFeed(feed)
    }
}