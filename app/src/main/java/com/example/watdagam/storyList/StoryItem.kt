package com.example.watdagam.storyList

data class StoryItem(
    val id: Long,
    val nickname: String,
    val latitude: Double,
    val longitude: Double,
    val content: String,
    val likes: Int,
    val distance: Double
)
