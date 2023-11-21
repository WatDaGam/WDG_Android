package com.example.watdagam.storyList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watdagam.R

class StoryAdapter(private val storyList: List<StoryItem>): RecyclerView.Adapter<StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder =
        StoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_story, parent, false))

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) =
        holder.bind(storyList[position])

    override fun getItemCount(): Int =
        storyList.size

    override fun getItemId(position: Int) = storyList[position].id
}