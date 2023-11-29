package com.example.watdagam.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.Signup.SignupActivity
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.main.MainActivity
import com.example.watdagam.utils.WDGLocationService
import com.example.watdagam.utils.storage.StorageService
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    fun hasCachedToken(context: Context): Boolean {
        val tokenService = StorageService.getInstance(context).getTokenService()
        return tokenService.getAccessToken().isNotEmpty() || tokenService.getRefreshToken().isNotEmpty()
    }

    fun moveToMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    private fun moveToSignupActivity(context: Context) {
        val intent = Intent(context, SignupActivity::class.java)
        context.startActivity(intent)
    }

    fun onKakaoLoginSuccess(context: Context, accessToken: String) {
        viewModelScope.launch {
            try {
                val response = WDGUserService.login(context, "KAKAO", accessToken)
                when (response.code()) {
                    200 -> moveToMainActivity(context)
                    201 -> moveToSignupActivity(context)
                    else -> throw Exception("Fail on wdg login")
                }
            } catch (e: Exception) {
                Log.e("WDG_login_activity", e.message ?: "")
                Toast.makeText(context, "로그인 하지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun onKakaoLoginFailure(context: Context) {
        Toast.makeText(context, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
    }

    fun startLocationTracking(activity: AppCompatActivity) {
        val fineLocPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocPermission == PackageManager.PERMISSION_DENIED) {
            return
        }
        val locationService = WDGLocationService.getInstance(activity)
        locationService.startLocationTracking()
    }

}