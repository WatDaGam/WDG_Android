package com.example.watdagam

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

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

        loginButtonKakao.setOnClickListener{
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        val tag = "KakaoLogin"

        //로그인 성공시 호출될 함수
        fun loginWithAccessToken(accessToken: String) {
            Toast.makeText(this, "access token: $accessToken", Toast.LENGTH_SHORT).show()
        }

        // 로그인 실패시 호출될 함수
        fun loginFail() {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
        }

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(tag, "카카오계정으로 로그인 실패", error)
                loginFail()
            } else if (token != null) {
                Log.i(tag, "카카오계정으로 로그인 성공 ${token.accessToken}")
                loginWithAccessToken(token.accessToken)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(tag, "카카오톡으로 로그인 실패", error)
                    loginFail()

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i(tag, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    loginWithAccessToken(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }
}