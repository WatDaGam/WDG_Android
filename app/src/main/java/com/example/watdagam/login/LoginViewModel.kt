package com.example.watdagam.login

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.R
import com.example.watdagam.Signup.SignupActivity
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.main.MainActivity
import com.example.watdagam.utils.WDGLocationService
import com.example.watdagam.utils.storage.StorageService
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

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

    fun loadUserData(activity: AppCompatActivity) {
        viewModelScope.launch {
            try {
                val tokenService = StorageService.getInstance(activity).getTokenService()
                if (tokenService.getAccessToken().isEmpty() && tokenService.getRefreshToken().isEmpty()) {
                    startAnimation(activity)
                    return@launch
                }
                val response = WDGUserService.getUserInfo(activity)
                val profileService = StorageService.getInstance(activity).getProfileService()
                if (response.isSuccessful) {
                    val body = response.body() ?: throw Exception("Response has no body")
                    profileService.nickname = body.nickname
                    profileService.posts = body.storyNum
                    profileService.likes = body.likeNum
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                } else if (response.code() == 401) {
                    startAnimation(activity)
                    return@launch
                } else {
                    throw Exception("Response is not Successful")
                }
            } catch (e: Exception) {
                Log.e("WDG_login_activity", e.toString())
            }
        }
    }

    private fun startAnimation(activity: AppCompatActivity) {
        val logo = activity.findViewById<ImageView>(R.id.logo)
        val containerLoginButtons = activity.findViewById<LinearLayout>(R.id.container_login_buttons)
        val loginAnimation = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(logo, "translationY", -300f).apply {
                startDelay = 1000
                duration = 1500
            }).before(ObjectAnimator.ofFloat(containerLoginButtons, View.ALPHA,  1f).apply {
                startDelay = 500
                duration = 500
            })
        }
        loginAnimation.start()
    }

}