package com.example.watdagam.fragments

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.LoginActivity
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
                ApiService.user_data_pref.nickname = updatedProfile.nickname
                ApiService.user_data_pref.posts = updatedProfile.post
                ApiService.user_data_pref.likes = updatedProfile.likes
                profile.value = updatedProfile
            } catch (e: RuntimeException) {
                Log.e("WDG_MY_PAGE", e.message ?: "(no error message)")
            }
        }
    }

    fun logout(context: Context) {
        ApiService.token_pref.setAccessToken("", 0)
        ApiService.token_pref.setRefreshToken("", 0)
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    fun withdraw(context: Context) {
        viewModelScope.launch {
            val apiService = ApiService.getInstance(context)
            apiService.withdrawal(
                context,
                onSuccess = { _, response ->
                    when (response.code()) {
                        200 -> {
                            Toast.makeText(context, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                        400 -> {
                            Toast.makeText(context, "로그인 정보가 만료되었습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                        else -> {
                            Log.e("WDG_API", "Unhandled Response code ${response.code()}")
                        }
                    }
                },
                onFailure = { _, _ ->  },
            )
        }
    }
}