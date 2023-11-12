package com.example.watdagam

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.watdagam.databinding.ListItemStoryBinding
import java.util.Date

data class StoryDto(
    val createdAt: Date,
    val lati: Double,
    val longi: Double,
    val nickname: String,
    val id: Long,
    val userId: Int,
    val content: String,
    val likeNum: Int,
)

data class Story(
    val createdAt: Date,
    val lati: Double,
    val longi: Double,
    val nickname: String,
    val id: Long,
    val userId: Int,
    val content: String,
    val likeNum: Int,
    val distance: Double,
)

class StoryAdapter(val datas: List<Story>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        StoryViewHolder(ListItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("WDG_ADAPTER", "onBindViewHolder: $position")
        val binding = (holder as StoryViewHolder).binding
        val story = datas[position]

        binding.message.text = story.content
        binding.likes.text = story.likeNum.toString()
        binding.distance.text = story.distance.toString()

        binding.container.setOnClickListener { view: View ->
            Log.d("WDG_ADAPTER", "onClick: $position")
            Toast.makeText(view.context, story.content, Toast.LENGTH_SHORT).show()
        }
    }
}