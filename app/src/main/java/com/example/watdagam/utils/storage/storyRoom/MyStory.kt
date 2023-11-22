package com.example.watdagam.utils.storage.storyRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyStory(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "nickname") val nickname: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "likes") val likes: Int
)
