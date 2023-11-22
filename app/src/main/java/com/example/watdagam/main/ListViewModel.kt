package com.example.watdagam.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGStoryService
import com.example.watdagam.storyList.StoryItem
import com.example.watdagam.utils.WDGLocationService
import kotlinx.coroutines.launch

class ListViewModel: ViewModel() {
    companion object {
        private const val TAG = "WDG_listViewModel"
    }
    private val _currentAddress = MutableLiveData<Address>()
    private val _storyItemList = MutableLiveData<List<StoryItem>>()
    var lastAddress: Address? = null

    fun getCurrentAddress() = _currentAddress
    fun getStoryItemList() = _storyItemList

    fun reloadLocation(activity: Activity) {
        val locationService = WDGLocationService.getInstance(activity)
        val fineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(activity, "글을 남기기 위해서는 자세한 위치 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            locationService.requestLocationPermissions(activity)
        } else {
            lastAddress = null
            locationService.updateLocation()
        }
    }

    fun startLocationTracking(activity: Activity, owner: LifecycleOwner) {
        val locationService = WDGLocationService.getInstance(activity)
        val fineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(activity, "글을 남기기 위해서는 자세한 위치 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            locationService.requestLocationPermissions(activity)
        } else {
            locationService.startLocationTracking()
            locationService.getAddress().observe(owner) { address ->
                _currentAddress.postValue(address)
                updateAddress(activity, address)
            }
        }
    }

    private fun updateAddress(context: Context, address: Address) {
        val distance = FloatArray(3)
        if (lastAddress != null) {
            Location.distanceBetween(
                lastAddress!!.latitude, lastAddress!!.longitude,
                address.latitude, address.longitude, distance
            )
        }

        if (_storyItemList.value == null || lastAddress == null || distance[0] > 30f) {
            lastAddress = address
            fetchNewStoryList(context, address)
        } else {
            updateListDistance(_storyItemList.value!!, lastAddress!!)
        }
    }

    private fun fetchNewStoryList(context: Context, address: Address) {
        viewModelScope.launch {
            try {
                val response = WDGStoryService.getStoryList(context, address.latitude, address.longitude)
                if (response.isSuccessful) {
                    val storyItemList = response.body()!!.stories.map { storyDto ->
                        StoryItem(
                            storyDto.id,
                            storyDto.lati,
                            storyDto.longi,
                            String.format("%s 왔다감", storyDto.nickname),
                            storyDto.content,
                            String.format("%.4f %.4f", storyDto.lati, storyDto.longi),
                            String.format("%d", storyDto.likeNum),
                        )
                    }
                    updateListDistance(storyItemList, address)
                } else {
                    throw Exception("Response isn't Successful")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fail to fetch new stories cause ${e.message} ${e.cause}")
                Toast.makeText(context, "리스트 데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
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
            storyItem.distance =
                if (distance[0] > 1_000f) {
                    String.format("%.3f km", distance[0] / 1_000f)
                } else if (distance [0] != 0f) {
                    String.format("%.3f m", distance[0])
                } else {
                    "0.0 m"
                }
        }
        _storyItemList.postValue(prevList)
    }
}