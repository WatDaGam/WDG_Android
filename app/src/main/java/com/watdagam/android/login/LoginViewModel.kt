package com.watdagam.android.login

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.watdagam.android.R
import com.watdagam.android.Signup.SignupActivity
import com.watdagam.android.main.MainActivity
import com.watdagam.android.utils.WDGLocationService
import com.watdagam.android.utils.api.KakaoApi
import com.watdagam.android.utils.api.UserService
import com.watdagam.android.utils.storage.StorageService
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel: ViewModel() {

    fun checkUser(activity: AppCompatActivity) {
        val tokenService = StorageService.getInstance(activity).getTokenService()
        if (tokenService.getAccessToken().isEmpty() &&
            tokenService.getRefreshToken().isEmpty()) {
            val kakaoLoginButton = activity.findViewById<ImageButton>(R.id.login_button_kakao)
            kakaoLoginButton.setOnClickListener { kakaoLogin(activity) }
            startAnimation(activity)
        } else {
            viewModelScope.launch {
                loadUserData(activity)
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            }
        }
    }

    private fun kakaoLogin(activity: AppCompatActivity) {
        val containerLoginButtons = activity.findViewById<LinearLayout>(R.id.container_login_buttons)
        viewModelScope.launch {
            lateinit var kakaoToken: OAuthToken
            lateinit var response: Response<Void>
            try {
                containerLoginButtons.alpha = 0f
                kakaoToken = KakaoApi.login(activity)
                Log.d("WDG_loginViewModel", "kakaoToken: $kakaoToken")
            } catch (error: Throwable) {
                containerLoginButtons.alpha = 1f
                Log.e("WDG_login_activity", "Kakao login fail $error")
                Toast.makeText(activity, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
                return@launch
            }
            try {
                response = UserService.login(activity, "KAKAO", kakaoToken.accessToken)
                if (!response.isSuccessful) {
                    throw RuntimeException("Response is not successful")
                }
            } catch (error: Throwable) {
                containerLoginButtons.alpha = 1f
                Log.e("WDG_login_activity", "WDG Login fail $error")
                Toast.makeText(activity, "로그인 하지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            when (response.code()) {
                200 -> {
                    // login 성공, 사용자 정보 fetch, 리스트 페이지로 이동
                    loadUserData(activity)
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                }
                201 -> {
                    // login 성공, 새로운 사용자, 회원가입 페이지로 이동
                    val intent = Intent(activity, SignupActivity::class.java)
                    activity.startActivity(intent)
                }
            }
        }
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

    private suspend fun loadUserData(activity: AppCompatActivity) {
        try {
            val response = UserService.getUserInfo(activity)
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
                return
            } else {
                throw Exception("Response is not Successful")
            }
        } catch (e: Exception) {
            Log.e("WDG_login_activity", e.toString())
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