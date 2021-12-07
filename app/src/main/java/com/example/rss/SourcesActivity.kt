package com.example.rss

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivitySourcesBinding
import com.google.android.material.textfield.TextInputEditText
import android.util.DisplayMetrics




class SourcesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySourcesBinding
    private lateinit var sourceAdapter: SourceAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar by default
        supportActionBar?.hide()

        sourceAdapter = SourceAdapter(getSources())
        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = sourceAdapter
        }

        val viewOtherSourceBar = layoutInflater.inflate(R.layout.other_source_bar, null)
        val v: View = binding.mtb

        val transition: Transition = TransitionInflater
            .from(this)
            .inflateTransition(R.transition.fade_transition)

        val scene1 = Scene(binding.abl, viewOtherSourceBar)
        val scene2 = Scene(binding.abl, v)

        binding.mtb.menu.getItem(0).setOnMenuItemClickListener {
            TransitionManager.go(scene1, transition)
            true
        }

        viewOtherSourceBar.findViewById<ImageButton>(R.id.imgCancel).setOnClickListener {
            TransitionManager.go(scene2, transition)
        }

        



        /*
        //View from other_source_bar.xml
        val viewOtherSourceBar = layoutInflater.inflate(R.layout.other_source_bar, null)

        //View from material tool bar in activity_sources.xml
        val viewMaterialToolBar = binding.mtb

        //When first item in top_app_bar_sources.xml is pressed  all view in app bar layout is removes and
        //other_source_bar.xml is added
        binding.mtb.menu.getItem(0).setOnMenuItemClickListener {
            binding.abl.removeAllViews()
            binding.abl.addView(viewOtherSourceBar)
            true
        }

        //When imgCancel in other_source_bar.xml is pressed  all view in app bar layout is removes and
        //material tool bar in activity_sources.xml is added
        viewOtherSourceBar.findViewById<ImageButton>(R.id.imgCancel).setOnClickListener {
            binding.abl.removeAllViews()
            binding.abl.addView(viewMaterialToolBar)
        }
        */
    }

    private fun getSources(): MutableList<Source> {
        val sources = mutableListOf<Source>()
        val s1 = Source("Forbes Mexico", "forbesmexico.com/feed")
        val s2 = Source("Mientras tanto en Mexico", "mientrastantoenmexico.com/feed")
        val s3 = Source("Xataka Mexico", "xatakamexico.com/feed")
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        sources.add(s1)
        sources.add(s2)
        sources.add(s3)
        return sources
    }

}