package com.example.watdagam.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGStoryService
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.storyList.StoryItem
import com.example.watdagam.data.StoryDto
import com.example.watdagam.utils.storage.StorageService
import com.example.watdagam.utils.storage.storyRoom.MyStory
import com.example.watdagam.utils.WDGLocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivityViewModel: ViewModel() {
    companion object {
        private const val TAG = "WDG_profile_view_model"
    }

    private val _nickname = MutableLiveData<String>()
    private val _posts = MutableLiveData<Int>()
    private val _likes = MutableLiveData<Int>()
    private val _myStoryList = MutableLiveData<List<StoryItem>>()

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
                profileService.posts = profile.storyNum
                profileService.likes = profile.likeNum
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
                myStory.latitude,
                myStory.longitude,
                String.format("%s 왔다감", myStory.nickname),
                myStory.content,
                String.format("%.4f %.4f", myStory.latitude, myStory.longitude),
                String.format("%d", myStory.likes),
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

    fun startLocationTracking(activity: AppCompatActivity) {
        val locationService = WDGLocationService.getInstance(activity)
        val fineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(activity, "글을 남기기 위해서는 자세한 위치 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            locationService.requestLocationPermissions(activity)
        } else {
            locationService.startLocationTracking()
            locationService.getAddress().observe(activity) { address ->
                if (_myStoryList.value != null)
                    updateListDistance(_myStoryList.value!!, address)
            }
        }
    }

    private fun updateListDistance(prevList: List<StoryItem>, address: Address) {
        val distance = FloatArray(3)
        prevList.forEach { storyItem ->
            Location.distanceBetween(
                storyItem.latitude, storyItem.longitude,
                address.latitude, address.longitude, distance
            )
            storyItem.tooFar = distance[0] > 30f
            storyItem.distance =
                if (distance[0] > 100_000f) {
                    String.format("%d km", (distance[0] / 1_000f).toInt())
                } else if (distance [0] > 1_000f) {
                    String.format("%.1f km", distance[0] / 1_000f)
                } else {
                    String.format("%d m", distance[0].toInt())
                }
        }
        _myStoryList.postValue(prevList)
    }
}