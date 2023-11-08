package com.example.watdagam.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenSharedPreference(context: Context) {

    companion object {
        private const val PREFERENCE_FILENAME = "wdg-token-preference"
        private const val KEY_ACCESS_TOKEN = "access-token"
        private const val KEY_ACCESS_TOKEN_EXPIRATION_TIME = "access-token-expiration-time"
        private const val KEY_REFRESH_TOKEN = "refresh-token"
        private const val KEY_REFRESH_TOKEN_EXPIRATION_TIME = "refresh-token-expiration-time"

        private const val TAG = "TOKEN"
    }
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILENAME,0)

    private var _accessToken: String?
        get() = preferences.getString(KEY_ACCESS_TOKEN, "")
        set(value) = preferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    private var _accessTokenExpirationTime: Long
        get() = preferences.getLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, 0)
        set(value) = preferences.edit().putLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, value).apply()

    private var _refreshToken: String?
        get() = preferences.getString(KEY_REFRESH_TOKEN, "")
        set(value) = preferences.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    private var _refreshTokenExpirationTime: Long
        get() = preferences.getLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, 0)
        set(value) = preferences.edit().putLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, value).apply()

    fun getAccessToken(): String {
        if (_accessTokenExpirationTime - System.currentTimeMillis() < 10_000) {
            _accessToken = ""
            _accessTokenExpirationTime = 0
            Log.d(TAG, "access token expired")
        }
        return _accessToken?: ""
    }
    fun setAccessToken(token: String, expTime: Long) {
        _accessToken = token
        _accessTokenExpirationTime = expTime
        Log.d(TAG, "access token: $token expire at $expTime")
    }

    fun getRefreshToken(): String {
        if (_refreshTokenExpirationTime - System.currentTimeMillis() < 10_000) {
            _refreshToken = ""
            _refreshTokenExpirationTime = 0
            Log.d(TAG, "refresh token expired")
        }
        return _refreshToken?: ""
    }

    fun setRefreshToken(token: String, expTime: Long) {
        _refreshToken = token
        _refreshTokenExpirationTime = expTime
        Log.d(TAG, "refresh token: $token expire at $expTime")
    }
}