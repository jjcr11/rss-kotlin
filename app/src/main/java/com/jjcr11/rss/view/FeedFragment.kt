package com.jjcr11.rss.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jjcr11.rss.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private lateinit var mBinding: FragmentFeedBinding
    private lateinit var mAdapter: FeedItemAdapter
    private val mArgs: FeedFragmentArgs by navArgs()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFeedBinding.inflate(inflater, container, false)

        mAdapter = FeedItemAdapter(this, mArgs.feeds.toList())

        mBinding.vp.adapter = mAdapter

        mBinding.vp.apply {
            adapter = mAdapter
            //reduceDragSensitivity()
        }

        //mBinding.vp.setOnClickListener { Log.d("", "DDDDDDDDDDDDDDD")  }

        //mBinding.vp.In

        /*mBinding.vp.getChildAt(0).setOnTouchListener { view, motionEvent ->
            //Log.d("", "EEEEEEEEEEE")
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {

                    mVelocityTracker?.clear()
                    mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                    mVelocityTracker?.addMovement(motionEvent)
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    mVelocityTracker?.let {
                        val pointerId: Int = motionEvent.getPointerId(motionEvent.actionIndex)
                        it.addMovement(motionEvent)
                        it.computeCurrentVelocity(1000)
                        if(it.getXVelocity(pointerId) > it.getYVelocity(pointerId)) {
                            Log.d("", "X")
                            return@let false
                        } else {
                            Log.d("", "Y")
                            return@let false
                        }
                        //Log.d("", "X velocity: ${getXVelocity(pointerId)}")
                        //Log.d("", "Y velocity: ${getYVelocity(pointerId)}")
                    }
                    false
                    //true
                }

                MotionEvent.ACTION_UP -> {
                    true
                }

                else -> true
            }
        }*/

        /*mBinding.vp.getChildAt(0).setOnTouchListener { view, motionEvent ->
            true
        }*/

        return mBinding.root
    }
}

/*private fun ViewPager2.reduceDragSensitivity() {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop * 5)
}*/