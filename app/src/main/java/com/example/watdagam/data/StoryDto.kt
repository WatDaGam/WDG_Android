package com.example.watdagam.data

import java.util.Date

data class StoryDto (
    val createdAt: Date,
    val lati: Double,
    val longi: Double,
    val nickname: String,
    val id: Long,
    val userId: Int,
    val content: String,
    val likeNum: Int
)