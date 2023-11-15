package com.example.watdagam.storage

import android.content.Context
import androidx.room.Room
import com.example.watdagam.storage.storyRoom.MyStoryDao
import com.example.watdagam.storage.storyRoom.NearbyStoryDao
import com.example.watdagam.storage.storyRoom.StoryDatabase

class StorageService {
    companion object {
        private var instance: StorageService? = null
        private lateinit var _token_pref: TokenSharedPreference
        private lateinit var _profile_pref: ProfileSharedPreference
        private lateinit var _storyDB: StoryDatabase

        private const val TAG = "WDG_storage_service"

        fun getInstance(applicationContext: Context): StorageService {
            return instance ?: StorageService().also { it ->
                _token_pref = TokenSharedPreference(applicationContext)
                _profile_pref = ProfileSharedPreference(applicationContext)
                _storyDB = Room.databaseBuilder(
                    applicationContext,
                    StoryDatabase::class.java,
                    "wdg_story_db"
                ).build()
                instance = it
            }
        }
    }

    fun getTokenService(): TokenSharedPreference = _token_pref
    fun getProfileService(): ProfileSharedPreference = _profile_pref
    fun getNearbyStoryDao(): NearbyStoryDao = _storyDB.nearbyStoryDao()
    fun getMyStoryDao(): MyStoryDao = _storyDB.myStoryDao()

}