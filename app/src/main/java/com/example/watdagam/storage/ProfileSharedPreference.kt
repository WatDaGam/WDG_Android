package com.example.watdagam.storage

import android.content.Context
import android.content.SharedPreferences

class ProfileSharedPreference (context: Context) {

    companion object {
        private const val PREFERENCE_FILENAME = "user-data-preference"
        private const val KEY_NICKNAME = "key-nickname"
        private const val KEY_POSTS = "key-posts"
        private const val KEY_LIKES = "key-likes"
    }
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILENAME,0)

    var nickname: String?
        get() = preferences.getString(KEY_NICKNAME, "")
        set(value) = preferences.edit().putString(KEY_NICKNAME, value).apply()

    var posts: Int
        get() = preferences.getInt(KEY_POSTS, 0)
        set(value) = preferences.edit().putInt(KEY_POSTS, value).apply()

    var likes: Int
        get() = preferences.getInt(KEY_LIKES, 0)
        set(value) = preferences.edit().putInt(KEY_NICKNAME, value).apply()

}