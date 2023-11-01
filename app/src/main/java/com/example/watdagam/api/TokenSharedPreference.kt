package com.example.watdagam.api

import android.content.Context
import android.content.SharedPreferences

class TokenSharedPreference(context: Context) {
    private val prefFilename = "token_preferences"
    private val keyAccessToken = "accessToken"
    private val keyRefreshToken = "refreshToken"
    private val pref: SharedPreferences = context.getSharedPreferences(prefFilename,0)

    var accessToken: String?
        get() = pref.getString(keyAccessToken, "")
        set(value) = pref.edit().putString(keyAccessToken, value).apply()

    var refreshToken: String?
        get() = pref.getString(keyRefreshToken, "")
        set(value) = pref.edit().putString(keyRefreshToken, value).apply()
}