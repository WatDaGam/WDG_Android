package com.example.watdagam.storage

import android.content.Context

class StorageService {
    companion object {
        private var instance: StorageService? = null
        private lateinit var _token_pref: TokenSharedPreference
        private lateinit var _profile_pref: ProfileSharedPreference

        private const val TAG = "WDG_storage_service"

        fun getInstance(applicationContext: Context): StorageService {
            return instance ?: StorageService().also { it ->
                _token_pref = TokenSharedPreference(applicationContext)
                _profile_pref = ProfileSharedPreference(applicationContext)
                instance = it
            }
        }
    }

    fun getTokenService(): TokenSharedPreference = _token_pref
    fun getProfileService(): ProfileSharedPreference = _profile_pref

}