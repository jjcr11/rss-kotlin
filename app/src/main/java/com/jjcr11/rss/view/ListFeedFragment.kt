package com.jjcr11.rss.view

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjcr11.rss.R
import com.jjcr11.rss.data.model.AppDatabase
import com.jjcr11.rss.data.model.Feed
import com.jjcr11.rss.databinding.FragmentListFeedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListFeedFragment : Fragment(), FeedAdapterOnClick {

    private lateinit var mBinding: FragmentListFeedBinding
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val feeds = AppDatabase.getDatabase(requireContext()).dao().getFeedsByDate()
            mAdapter = FeedAdapter(feeds as MutableList<Feed>, this@ListFeedFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentListFeedBinding.inflate(inflater, container, false)

        mLayoutManager = LinearLayoutManager(requireContext())

        mBinding.rv.let {
            it.adapter = mAdapter
            it.layoutManager = mLayoutManager
        }

        mBinding.srl.setOnRefreshListener {
            mAdapter.clear()
            lifecycleScope.launch(Dispatchers.IO) {
                val feeds = AppDatabase.getDatabase(requireContext()).dao().getFeedsByDate()
                launch(Dispatchers.Main) {
                    feeds.forEach {
                        mAdapter.add(it)
                    }
                    mBinding.srl.isRefreshing = false
                }
            }
        }

        mBinding.fabe.setOnClickListener {
            findNavController().navigate(R.id.action_listFeedFragment_to_listSourceFragment)
        }

        val width = resources.displayMetrics.widthPixels
        //val noc = w * -0.4f

        mBinding.mtb.setNavigationOnClickListener {
            ObjectAnimator.ofFloat(mBinding.cl, "pivotX", 0f).let {
                it.duration = 0
                it.start()
            }
            ObjectAnimator.ofFloat(mBinding.cl, "scaleX", 0.9f).let {
                it.duration = 500
                it.start()
            }
            ObjectAnimator.ofFloat(mBinding.cl, "scaleY", 0.9f).let {
                it.duration = 500
                it.start()
            }
            ObjectAnimator.ofFloat(mBinding.cl, "translationX", (width * -0.4f)).let {
                it.duration = 500
                it.start()
            }
        }

        return mBinding.root
    }

    override fun onClick(feeds: List<Feed>, position: Int) {
        val action = ListFeedFragmentDirections.actionListFeedFragmentToFeedFragment(
            feeds.toTypedArray(),
            position
        )
        findNavController().navigate(action)
    }
}