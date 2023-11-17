package com.example.watdagam.data

data class StoryDto (
    val createdAt: String,
    val lati: Double,
    val longi: Double,
    val nickname: String,
    val id: Long,
    val userId: Int,
    val content: String,
    val likeNum: Int
)