package com.example.watdagam.api

import android.content.Context
import android.content.SharedPreferences

class TokenSharedPreference(context: Context) {

    companion object {
        private const val PREFERENCE_FILENAME = "wdg-token-preference"
        private const val KEY_ACCESS_TOKEN = "access-token"
        private const val KEY_ACCESS_TOKEN_EXPIRATION_TIME = "access-token-expiration-time"
        private const val KEY_REFRESH_TOKEN = "refresh-token"
        private const val KEY_REFRESH_TOKEN_EXPIRATION_TIME = "refresh-token-expiration-time"
    }
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILENAME,0)

    var accessToken: String?
        get() = preferences.getString(KEY_ACCESS_TOKEN, "")
        set(value) = preferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    var accessTokenExpirationTime: Long
        get() = preferences.getLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, 0)
        set(value) = preferences.edit().putLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, value).apply()

    var refreshToken: String?
        get() = preferences.getString(KEY_REFRESH_TOKEN, "")
        set(value) = preferences.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    var refreshTokenExpirationTime: Long
        get() = preferences.getLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, 0)
        set(value) = preferences.edit().putLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, value).apply()
}