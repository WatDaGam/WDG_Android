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

    private var accessToken: String?
        get() = preferences.getString(KEY_ACCESS_TOKEN, "")
        set(value) = preferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    private var accessTokenExpirationTime: Long
        get() = preferences.getLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, 0)
        set(value) = preferences.edit().putLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, value).apply()

    private var refreshToken: String?
        get() = preferences.getString(KEY_REFRESH_TOKEN, "")
        set(value) = preferences.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    private var refreshTokenExpirationTime: Long
        get() = preferences.getLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, 0)
        set(value) = preferences.edit().putLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, value).apply()

    fun getAccessToken(): String {
        if (accessTokenExpirationTime - System.currentTimeMillis() < 10_000) {
            accessToken = ""
            accessTokenExpirationTime = 0
        }
        return accessToken?: ""
    }
    fun setAccessToken(token: String, expTime: Long) {
        accessToken = token
        accessTokenExpirationTime = expTime
    }

    fun getRefreshToken(): String {
        if (refreshTokenExpirationTime - System.currentTimeMillis() < 10_000) {
            refreshToken = ""
            refreshTokenExpirationTime = 0
        }
        return refreshToken?: ""
    }

    fun setRefreshToken(token: String, expTime: Long) {
        refreshToken = token
        refreshTokenExpirationTime = expTime
    }
}