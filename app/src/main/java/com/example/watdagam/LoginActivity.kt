package com.example.watdagam

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.watdagam.api.ApiService
import com.example.watdagam.api.KakaoService
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var containerLoginButtons: LinearLayout
    private lateinit var loginButtonKakao: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logo = this.findViewById(R.id.logo)
        containerLoginButtons = this.findViewById(R.id.container_login_buttons)
        loginButtonKakao = this.findViewById(R.id.login_button_kakao)

        // Kakao SDK 초기화
        KakaoSdk.init(this, "27c267b47e61135cd098eb3fc9270bc6")

        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(logo, "translationY", -300f).apply {
                startDelay = 1000
                duration = 1500
            }).before(ObjectAnimator.ofFloat(containerLoginButtons, View.ALPHA, 0f, 1f).apply {
                startDelay = 500
                duration = 500
            })
        }.start()

        loginButtonKakao.setOnClickListener {
            val kakaoService = KakaoService.getInstance(this)
            kakaoService.login(
                onSuccess = { accessToken: String -> onKakaoLoginSuccess(accessToken) },
                onFailure = { -> onKakaoLoginFailure() },
            )
        }
    }

    private fun onKakaoLoginSuccess(accessToken: String) {
        val apiService: ApiService = ApiService.getInstance(this)
        apiService.login(
            "KAKAO",
            accessToken,
            onSuccess = { _: Call<Void>, response: Response<Void> -> onWDGLoginSuccess(response) },
            onFailure = { _: Call<Void>, _: Throwable -> onWDGLoginFailure() },
        )
    }

    private fun onKakaoLoginFailure() {
        Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
    }

    private fun onWDGLoginSuccess(response: Response<Void>) {
        if (response.isSuccessful.not()) {
            Log.e("WDG_LOGIN", "Fail $response")
            Toast.makeText(this, "로그인 하지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("WDG_LOGIN", "Success ${response.code()}")
            when (response.code()) {
                200 -> {
                    // Move to list page
                    val intent = Intent(this@LoginActivity, ListActivity::class.java)
                    startActivity(intent)
                }

                201 -> {
                    // Move to signup page
                    val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun onWDGLoginFailure() {
        Toast.makeText(this, "로그인 하지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }
}