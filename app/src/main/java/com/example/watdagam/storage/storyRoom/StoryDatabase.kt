package com.example.watdagam.storage.storyRoom

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MyStory::class], version = 1)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun myStoryDao(): MyStoryDao
}
