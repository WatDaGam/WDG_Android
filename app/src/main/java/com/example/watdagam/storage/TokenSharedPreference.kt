package com.example.watdagam.storage

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenSharedPreference(context: Context) {

    companion object {
        private const val PREFERENCE_FILENAME = "wdg-token-preference"
        private const val KEY_ACCESS_TOKEN = "access-token"
        private const val KEY_ACCESS_TOKEN_EXPIRATION_TIME = "access-token-expiration-time"
        private const val KEY_REFRESH_TOKEN = "refresh-token"
        private const val KEY_REFRESH_TOKEN_EXPIRATION_TIME = "refresh-token-expiration-time"

        private const val TAG = "WDG_TOKEN"
    }
    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val pref = EncryptedSharedPreferences.create(
        context,
        PREFERENCE_FILENAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private var _accessToken: String?
        get() = pref.getString(KEY_ACCESS_TOKEN, "")
        set(value) = pref.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    private var _accessTokenExpirationTime: Long
        get() = pref.getLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, 0)
        set(value) = pref.edit().putLong(KEY_ACCESS_TOKEN_EXPIRATION_TIME, value).apply()

    private var _refreshToken: String?
        get() = pref.getString(KEY_REFRESH_TOKEN, "")
        set(value) = pref.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    private var _refreshTokenExpirationTime: Long
        get() = pref.getLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, 0)
        set(value) = pref.edit().putLong(KEY_REFRESH_TOKEN_EXPIRATION_TIME, value).apply()

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