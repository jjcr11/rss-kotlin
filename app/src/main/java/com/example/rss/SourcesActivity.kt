package com.example.rss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.databinding.ActivitySourcesBinding
import com.google.android.material.textfield.TextInputEditText

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

        //View from other_source_bat.xml to do the transition
        val viewOtherSourceBar = layoutInflater.inflate(R.layout.other_source_bar, null)
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

        viewOtherSourceBar.findViewById<ImageButton>(R.id.imgCancel).setOnClickListener {
            TransitionManager.go(fromAppBarLayoutToViewMaterialToolBar, transition)
        }
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