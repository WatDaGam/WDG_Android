package com.example.watdagam.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.main.MainActivity
import com.example.watdagam.utils.WDGLocationService
import com.example.watdagam.utils.api.StoryService
import kotlinx.coroutines.launch

class PostActivityViewModel: ViewModel() {
    companion object {
        private const val TAG = "WDG_POST_ACTIVITY"
    }

    private val _address = MutableLiveData<Address>()
    fun getAddress() = _address

    fun postStory(activity: AppCompatActivity, content: String) {
        if (content.isBlank()) {
            Toast.makeText(activity, "남길 내용이 없습니다.", Toast.LENGTH_SHORT).show()
        } else if (_address.value == null) {
            Toast.makeText(activity, "위치를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            updateAddress(activity)
        } else {
            viewModelScope.launch {
                try {
                    val response = StoryService.uploadStory(
                        activity,
                        content,
                        _address.value!!.latitude,
                        _address.value!!.longitude
                    )
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "메세지를 남겼습니다.", Toast.LENGTH_SHORT).show()
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                    } else {
                        Toast.makeText(activity, "메세지를 남기지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: RuntimeException) {
                    Log.e(TAG, e.message ?: "no error message")
                }
            }
        }
    }

    fun updateAddress(activity: AppCompatActivity) {
        val locationService = WDGLocationService.getInstance(activity)
        if (locationService.getAddress().value != null) {
            _address.postValue(locationService.getAddress().value!!)
        }
        val fineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(activity, "글을 남기기 위해서는 자세한 위치 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            locationService.requestLocationPermissions(activity)
            activity.startActivity(Intent(activity, MainActivity::class.java))
        } else {
            locationService.getAddress().observe(activity) { lastAddress ->
                if (_address.value == null) {
                    _address.postValue(lastAddress)
                }
            }
            locationService.updateLocation()
        }
    }
}