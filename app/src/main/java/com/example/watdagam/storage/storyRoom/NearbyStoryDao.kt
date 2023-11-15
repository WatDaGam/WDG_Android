package com.example.watdagam.storage.storyRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NearbyStoryDao {
    @Query("SELECT * FROM nearbystory")
    fun getAll(): List<NearbyStory>

    @Query("DELETE FROM nearbystory")
    fun deleteAll()

    @Insert
    fun insertStory(vararg story: NearbyStory)

    @Update
    fun updateStory(story: NearbyStory)

    @Delete
    fun delete(story: NearbyStory)
}