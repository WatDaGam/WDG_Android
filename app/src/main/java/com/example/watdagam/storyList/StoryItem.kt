package com.example.watdagam.storyList

data class StoryItem(
    val id: Long,
    val latitude: Double,
    val longitude: Double,

    var title: String,
    var content: String,
    var location: String,
    var likes: String,
    var distance: String = "0.0 m",
    var tooFar: Boolean = true,
    var isExpanded: Boolean = false
)
