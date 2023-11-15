package com.example.watdagam.storyList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.watdagam.databinding.ListItemStoryBinding

class StoryAdapter(val datas: List<StoryItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        StoryViewHolder(ListItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as StoryViewHolder).binding
        val story = datas[position]

        binding.message.text = story.content
        binding.likes.text = story.likes.toString()
        binding.distance.text = story.distance.toString()

        binding.container.setOnClickListener { view: View ->
            Log.d("WDG_ADAPTER", "onClick: $position")
            Toast.makeText(view.context, story.content, Toast.LENGTH_SHORT).show()
        }
    }
}