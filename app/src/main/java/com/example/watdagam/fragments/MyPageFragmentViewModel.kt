package com.example.watdagam.fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.ApiService
import com.example.watdagam.api.UserInfo
import kotlinx.coroutines.launch

class MyPageFragmentViewModel: ViewModel() {
    private val profile: MutableLiveData<UserInfo> by lazy {
        MutableLiveData<UserInfo>().also {
            profile.value = UserInfo(
                ApiService.user_data_pref.nickname?: "",
                ApiService.user_data_pref.posts,
                ApiService.user_data_pref.likes
            )
        }
    }
    @JvmName("callFromUserInfo")
    fun getProfile(): MutableLiveData<UserInfo> {
        return profile
    }
    fun loadProfile(context: Context) {
        val cachedProfile = UserInfo(
            ApiService.user_data_pref.nickname?: "",
            ApiService.user_data_pref.posts,
            ApiService.user_data_pref.likes
        )
        if (!profile.equals(cachedProfile)) {
            profile.value = cachedProfile
        }
        viewModelScope.launch {
            try {
                val apiService = ApiService.getInstance(context)
                val updatedProfile = apiService.getUserInfo(context)
                profile.value = updatedProfile
            } catch (e: RuntimeException) {
                Log.e("WDG_MY_PAGE", e.message ?: "(no error message)")
            }
        }
    }
}