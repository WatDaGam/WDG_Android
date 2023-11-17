package com.example.watdagam.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.login.LoginActivity
import com.example.watdagam.post.PostActivity
import com.example.watdagam.storage.StorageService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


class MainActivityViewModel: ViewModel() {

    fun logout(context: Context) {
        val tokenService = StorageService.getInstance(context).getTokenService()
        tokenService.setAccessToken("", 0)
        tokenService.setRefreshToken("", 0)
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
    }

    fun withdraw(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setMessage("회원탈퇴 하시겠습니까?")
            .setNegativeButton("아니요", null)
            .setPositiveButton("네") {_, _ ->
                viewModelScope.launch {
                    try {
                        WDGUserService.withdraw(context)
                        Toast.makeText(context, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: RuntimeException) {
                        Log.e(TAG, e.message ?: "(no error message)")
                    }
                }
            }
            .create()
        dialog.show()
    }

    companion object {
        private const val TAG = "WDG_main_activity"
        private const val REQUEST_LOCATION = 1
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback
    }

    private val _lastLocation = MutableLiveData<Location>()
    private val _lastAddress = MutableLiveData<Address>()
    fun getLastAddress(): MutableLiveData<Address> = _lastAddress

    fun setLastLocation(context: Context, location: Location) {
        _lastLocation.postValue(location)
        val geocoder = Geocoder(context, Locale.KOREA)
        if (Build.VERSION.SDK_INT < 33) {
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addressList?.first() ?: Address(Locale.KOREA).also {
                it.latitude = location.latitude
                it.longitude = location.longitude
                it.locality = "???"
                Log.e(TAG, "지명을 가져올 수 없습니다")
            }
            _lastAddress.postValue(address)
        } else {
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) { addressList: List<Address>? ->
                val address = addressList?.first() ?: Address(Locale.KOREA).also {
                    it.latitude = location.latitude
                    it.longitude = location.longitude
                    it.locality = "???"
                    Log.e(TAG, "지명을 가져올 수 없습니다")
                }
                _lastAddress.postValue(address)
            }
        }
    }

    @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    fun reloadLocation(context: Context) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { setLastLocation(context, it) }
    }

    @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    fun startLocationTracking(context: Context) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.Builder(3_000)
            .setIntervalMillis(5_000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val currentLocation = p0.lastLocation
                if (currentLocation != null) {
                    if (_lastLocation.value == null ||
                        _lastLocation.value!!.distanceTo(currentLocation) > 30_000) {
                        setLastLocation(context, currentLocation)
                    }
                }
                Log.d(TAG, p0.lastLocation.toString())
                super.onLocationResult(p0)
            }
        }
        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
    }

    fun startPostActivity(activity: Activity) {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(activity, "글을 남기기 위해서는 자세한 위치 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            requestLocationPermissions(activity)
            return
        }
        val client = LocationServices.getFusedLocationProviderClient(activity)
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location ->
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

    private fun getLocationName(address: Address): String {
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