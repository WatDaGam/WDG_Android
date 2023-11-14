package com.example.watdagam.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.ApiService
import com.example.watdagam.storage.StorageService
import kotlinx.coroutines.launch

class ProfileActivityViewModel: ViewModel() {
    companion object {
        private const val TAG = "WDG_profile_view_model"
    }

    private val _nickname: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val _posts: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    private val _likes: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    fun getNickname(): MutableLiveData<String> = _nickname
    fun getPosts(): MutableLiveData<Int> = _posts
    fun getLikes(): MutableLiveData<Int> = _likes

    fun loadProfile(context: Context) {
        // Shared Preference 에 저장된 데이터를 우선적으로 표시
        val profileService = StorageService.getInstance(context).getProfileService()
        _nickname.postValue(profileService.nickname)
        _posts.postValue(profileService.posts)
        _likes.postValue(profileService.likes)

        viewModelScope.launch {
            try {
                // API를 통해 서버의 데이터 동기화
                val apiService = ApiService.getInstance(context)
                val profile = apiService.getUserInfo(context)

                // Shared Preference 갱신
                profileService.nickname = profile.nickname
                profileService.posts = profile.post
                profileService.likes = profile.likes

                // 서버 데이터 적용
                _nickname.postValue(profile.nickname)
                _posts.postValue(profile.post)
                _likes.postValue(profile.likes)
            } catch (e: Exception) {
                Log.e(TAG, "load profile failed cause ${e.message}")
                Toast.makeText(context, "서버로부터 데이터를 동기화하지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}