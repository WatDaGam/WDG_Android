package com.example.watdagam.storage.storyRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyStory(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "nickname") val nickname: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "likes") val likes: Int
)
