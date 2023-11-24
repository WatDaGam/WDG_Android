package com.example.watdagam.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class WDGLocationService {
    companion object {
        private const val TAG = "WDG_LocationService"
        private const val REQUEST_LOCATION = 1
        private val location = MutableLiveData<Location>()
        private val address = MutableLiveData<Address>()

        private var instance: WDGLocationService? = null
        private lateinit var appContext: Context
        private lateinit var client: FusedLocationProviderClient
        private lateinit var geocoder: Geocoder
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback
        fun getInstance(context: Context): WDGLocationService {
            return instance ?: WDGLocationService().also {
                appContext = context.applicationContext
                client = LocationServices.getFusedLocationProviderClient(appContext)
                geocoder = Geocoder(appContext, Locale.KOREA)
                locationRequest = LocationRequest.Builder(3_000)
                    .setIntervalMillis(5_000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()
                locationCallback = object: LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        val lastLocation = p0.lastLocation
                        if (lastLocation != null) {
                            Log.d(TAG, "lastLocation: ${lastLocation.latitude} ${lastLocation.longitude}")
                            location.postValue(lastLocation!!)
                            it.updateAddress(lastLocation)
                        }
                        super.onLocationResult(p0)
                    }
                }
                instance = it
            }
        }
    }

//    fun getLocation(): MutableLiveData<Location> = location
    fun getAddress(): MutableLiveData<Address> = address

    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    fun updateLocation() {
        client
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { lastLocation: Location ->
                location.postValue(lastLocation)
                updateAddress(lastLocation)
            }
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    fun startLocationTracking() {
        client.removeLocationUpdates(locationCallback)
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun updateAddress(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lastAddress =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)?.first()
                        ?: throw Exception("Cannot fetch location")
                lastAddress.latitude = location.latitude
                lastAddress.longitude = location.longitude
                lastAddress.featureName =
                    if (lastAddress.thoroughfare.isNullOrBlank().not()) {
                        lastAddress.thoroughfare
                    } else if (lastAddress.subLocality.isNullOrBlank().not()) {
                        lastAddress.subLocality
                    } else if (lastAddress.locality.isNullOrBlank().not()) {
                        lastAddress.locality
                    } else if (lastAddress.subAdminArea.isNullOrBlank().not()) {
                        lastAddress.subAdminArea
                    } else if (lastAddress.adminArea.isNullOrBlank().not()) {
                        lastAddress.adminArea
                    } else {
                        lastAddress.countryName
                    }
                address.postValue(lastAddress)
            } catch (e: Exception) {
                Log.e(TAG, "Fail to get address cause ${e.message} ${e.cause}")
            }
        }
    }
}