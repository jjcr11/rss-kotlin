package com.reader.rss

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingAdapter(fa: FragmentActivity, private val pages: Int): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return pages
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> OnBoardingFragment1("Welcome to RSS reader :)")
            1 -> OnBoardingFragment2()
            2 -> OnBoardingFragment3()
            3 -> OnBoardingFragment4()
            4 -> OnBoardingFragment1("Enjoy the app :)")
            else -> OnBoardingFragment1("")
        }
    }
}