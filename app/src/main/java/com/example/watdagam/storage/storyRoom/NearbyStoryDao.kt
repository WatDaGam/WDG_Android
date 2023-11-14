package com.example.watdagam.storage.storyRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NearbyStoryDao {
    @Query("SELECT * FROM nearbystory")
    fun getAll(): List<MyStory>

    @Query("SELECT * FROM nearbystory WHERE id IN (:storyId)")
    fun loadAllByIds(storyId: Int): List<MyStory>

    @Insert
    fun insertAll(vararg users: MyStory)

    @Delete
    fun delete(user: MyStory)
}