package com.example.watdagam.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.LoginActivity
import com.example.watdagam.api.ApiService
import com.example.watdagam.data.UserInfo
import com.example.watdagam.post.PostActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class MainActivityViewModel: ViewModel() {

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

    companion object {
        private const val TAG = "WDG_LOCATION_SERVICE"
        private const val REQUEST_LOCATION = 1
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback
    }

    private val _currentLocation = MutableLiveData<Location>()
    private val _lastLocation = MutableLiveData<Location>()
    private val _listAddress = MutableLiveData<Address>()

    fun getCurrentLocation(): MutableLiveData<Location> = _currentLocation
    fun getLastLocation(): MutableLiveData<Location> = _lastLocation
    fun getListAddress(): MutableLiveData<Address> = _listAddress

    fun reloadLocation(activity: Activity) {
        if (
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                LOCATION_PERMISSIONS[0],
            ) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                LOCATION_PERMISSIONS[1],
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Toast.makeText(
                activity.applicationContext,
                "앱을 사용하기 위해서는 자세한 위치 사용 권한이 필요합니다.",
                Toast.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                activity,
                LOCATION_PERMISSIONS,
                REQUEST_LOCATION
            )
        } else {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                activity.applicationContext,
            )
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location  ->
                    _currentLocation.postValue(location)
                }
        }
    }

    fun startLocationTracking(activity: Activity) {
        if (
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                LOCATION_PERMISSIONS[0],
            ) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                LOCATION_PERMISSIONS[1],
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Toast.makeText(
                activity.applicationContext,
                "앱을 사용하기 위해서는 자세한 위치 사용 권한이 필요합니다.",
                Toast.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                activity,
                LOCATION_PERMISSIONS,
                REQUEST_LOCATION
            )
        } else {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                activity.applicationContext,
            )
            locationRequest = LocationRequest.Builder(3_000)
                .setIntervalMillis(5_000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    _currentLocation.postValue(p0.lastLocation)
                    Log.d(TAG, p0.lastLocation.toString())
                    super.onLocationResult(p0)
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun setListAddress(context: Context, location: Location) {
        try {
            val geocoder = Geocoder(context, Locale.KOREA)
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
            val address = addressList[0]
            _listAddress.postValue(address)
        } catch (e: IOException) {
            val unknownAddress = Address(Locale.KOREA)
            unknownAddress.latitude = location.latitude
            unknownAddress.longitude = location.longitude
            unknownAddress.countryName = "???"
            _listAddress.postValue(unknownAddress)
            Log.e(TAG, "지명을 가져올 수 없습니다.")
        }
    }

    fun startPostActivity(activity: Activity) {
        if (
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                LOCATION_PERMISSIONS[0],
            ) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                LOCATION_PERMISSIONS[1],
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Toast.makeText(
                activity.applicationContext,
                "앱을 사용하기 위해서는 자세한 위치 사용 권한이 필요합니다.",
                Toast.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                activity,
                LOCATION_PERMISSIONS,
                REQUEST_LOCATION
            )
        } else {
            viewModelScope.launch {
                val client = LocationServices.getFusedLocationProviderClient(activity)
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        lateinit var address: Address
                        try {
                            val geocoder = Geocoder(activity, Locale.KOREA)
                            val addressList = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as List<Address>
                            address = addressList[0]
                        } catch (e: IOException) {
                            address = Address(Locale.KOREA)
                            address.latitude = location.latitude
                            address.longitude = location.longitude
                            address.countryName = "???"
                            Log.e(TAG, "지명을 가져올 수 없습니다.")
                        } finally {
                            val intent = Intent(activity, PostActivity::class.java)
                            val locationName = getLocationName(address)
                            intent.putExtra("KEY_WDG_ADDRESS", locationName)
                            intent.putExtra("KEY_WDG_LATITUDE", address.latitude)
                            intent.putExtra("KEY_WDG_LONGITUDE", address.longitude)
                            activity.startActivity(intent)
                        }
                    }
            }
        }
    }

    fun getLocationName(address: Address): String {
        val name =
        if (!address.thoroughfare.isNullOrBlank()) {
            address.thoroughfare
        } else if (!address.subLocality.isNullOrBlank()) {
            address.subLocality
        } else if (!address.locality.isNullOrBlank()) {
            address.locality
        } else if (!address.subAdminArea.isNullOrBlank()) {
            address.subAdminArea
        } else if (!address.adminArea.isNullOrBlank()) {
            address.adminArea
        } else {
            address.countryName
        }
        return name
    }
}