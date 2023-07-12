package com.jjcr11.rss.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.AppDatabase
import com.jjcr11.rss.databinding.FragmentFeedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private lateinit var mBinding: FragmentFeedBinding
    private lateinit var mAdapter: FeedItemAdapter
    private val mArgs: FeedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFeedBinding.inflate(inflater, container, false)

        val feeds = mArgs.feeds.toList()
        var feed = feeds[mArgs.position]

        mAdapter = FeedItemAdapter(this, feeds)

        mBinding.vp.adapter = mAdapter


        mBinding.vp.let {
            it.adapter = mAdapter
            it.currentItem = mArgs.position
            it.setPageTransformer { _, _ ->
                val index = it.currentItem
                feed = feeds[index]
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).dao().changeFeedRead(true, feed.id)
                }
                mBinding.mtb.menu[0].icon = getIcon(feed.saved)
            }
        }

        mBinding.mtb.menu[0].setOnMenuItemClickListener {
            val saved = !feed.saved
            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(requireContext()).dao().changeFeedSaved(saved, feed.id)
                feed.saved = saved
                launch(Dispatchers.Main) {
                    it.icon = getIcon(saved)
                }
            }
            true
        }

        mBinding.mtb.menu[1].setOnMenuItemClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(feed.link)))
            true
        }

        mBinding.mtb.menu[2].setOnMenuItemClickListener {
            val sendIntent: Intent = Intent().also {
                it.action = Intent.ACTION_SEND
                it.putExtra(Intent.EXTRA_TEXT, feed.link)
                it.type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, null))
            true
        }

        return mBinding.root
    }

    private fun getIcon(saved: Boolean): Drawable = if (saved) {
        resources.getDrawable(R.drawable.ic_bookmark, null)
    } else {
        resources.getDrawable(R.drawable.ic_bookmark_border, null)
    }
}