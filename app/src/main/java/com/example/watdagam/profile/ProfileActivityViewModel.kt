package com.example.watdagam.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGStoryService
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.storyList.StoryItem
import com.example.watdagam.data.StoryDto
import com.example.watdagam.storage.StorageService
import com.example.watdagam.storage.storyRoom.MyStory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val _myStoryList: MutableLiveData<List<StoryItem>> by lazy {
        MutableLiveData<List<StoryItem>>()
    }

    fun getNickname(): MutableLiveData<String> = _nickname
    fun getPosts(): MutableLiveData<Int> = _posts
    fun getLikes(): MutableLiveData<Int> = _likes
    fun getMyStoryList(): MutableLiveData<List<StoryItem>> = _myStoryList

    fun loadData(context: Context) {
        // Shared Preference 에 저장된 데이터를 우선적으로 표시
        val storageService = StorageService.getInstance(context)
        val profileService = storageService.getProfileService()
        val myStoryDao = storageService.getMyStoryDao()
        _nickname.postValue(profileService.nickname)
        _posts.postValue(profileService.posts)
        _likes.postValue(profileService.likes)
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                _myStoryList.postValue(myStoryListToStoryItemList(myStoryDao.getAll()))
            }
        }

        viewModelScope.launch {
            try {
                // API를 통해 서버의 데이터 동기화
                val profile = WDGUserService.getUserInfo(context).body() ?: throw Exception("Cannot fetch user info")
                val myStoryList = WDGStoryService.getMyStoryList(context).body()?.stories ?: throw Exception("Cannot fetch my story list")

                // Shared Preference 갱신
                profileService.nickname = profile.nickname
                profileService.posts = profile.post
                profileService.likes = profile.likes
                _nickname.postValue(profileService.nickname)
                _posts.postValue(profileService.posts)
                _likes.postValue(profileService.likes)


                // Room DB 갱신
                CoroutineScope(Dispatchers.IO).launch {
                    myStoryDao.deleteAll()
                    myStoryList.forEach { storyDto ->
                        myStoryDao.insertStory(storyDtoToMyStory(storyDto))
                    }
                    _myStoryList.postValue(myStoryListToStoryItemList(myStoryDao.getAll()))
                }
            } catch (e: Exception) {
                Log.e(TAG, "${e.message} ${e.cause}")
                Toast.makeText(context, "서버로부터 데이터를 동기화하지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun myStoryListToStoryItemList(myStoryList: List<MyStory>): List<StoryItem> {
        return myStoryList.map { myStory ->
            StoryItem(
                myStory.id,
                myStory.nickname,
                myStory.latitude,
                myStory.longitude,
                myStory.content,
                myStory.likes,
                0.0
            )
        }
    }

    private fun storyDtoToMyStory(storyDto: StoryDto): MyStory {
        return MyStory(
            storyDto.id,
            storyDto.nickname,
            storyDto.lati,
            storyDto.longi,
            storyDto.content,
            storyDto.likeNum
        )
    }
}