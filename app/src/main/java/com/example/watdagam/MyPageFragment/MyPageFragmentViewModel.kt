package com.example.watdagam.MyPageFragment

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
    private val _profile = MutableLiveData(UserInfo("", 0, 0))
    fun getProfile(): MutableLiveData<UserInfo> {
        return _profile
    }

    fun loadProfile(context: Context) {
        val cachedProfile = UserInfo(
            ApiService.user_data_pref.nickname?: "",
            ApiService.user_data_pref.posts,
            ApiService.user_data_pref.likes
        )
        _profile.postValue(cachedProfile)
        viewModelScope.launch {
            try {
                val apiService = ApiService.getInstance(context.applicationContext)
                val updatedProfile = apiService.getUserInfo(context)
                ApiService.user_data_pref.nickname = updatedProfile.nickname
                ApiService.user_data_pref.posts = updatedProfile.post
                ApiService.user_data_pref.likes = updatedProfile.likes
                _profile.postValue(updatedProfile)
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
            try {
                val apiService = ApiService.getInstance(context)
                apiService.withdrawal(context)
                Toast.makeText(context, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            } catch (e: RuntimeException) {
                Log.e("WDG_MY_PAGE", e.message ?: "(no error message)")
            }
        }
    }
}