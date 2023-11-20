package com.example.watdagam.storyList

data class StoryItem(
    val id: Long,
    val nickname: String,
    val latitude: Double,
    val longitude: Double,
    val content: String,
    var likes: Int,
    var distance: Double,
)
