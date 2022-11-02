package com.reader.rss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reader.rss.databinding.FragmentOnBoarding1Binding

class OnBoardingFragment1(private val text: String) : Fragment() {

    private lateinit var binding: FragmentOnBoarding1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoarding1Binding.inflate(layoutInflater, container, false)
        binding.tv.text = text
        return binding.root
    }
}