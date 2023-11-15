package com.example.watdagam.storage.storyRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MyStoryDao {
    @Query("SELECT * FROM mystory")
    fun getAll(): List<MyStory>

    @Query("DELETE FROM mystory")
    fun deleteAll()

    @Insert
    fun insertStory(vararg story: MyStory)

    @Update
    fun updateStory(story: MyStory)

    @Delete
    fun delete(story: MyStory)
}