package com.example.watdagam.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.example.watdagam.R
import com.example.watdagam.utils.KakaoLoginService
import java.util.Timer
import java.util.TimerTask

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var logo: ImageView
    private lateinit var containerLoginButtons: LinearLayout
    private lateinit var loginButtonKakao: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        KakaoLoginService.initializeSdk(this)

        logo = this.findViewById(R.id.logo)
        containerLoginButtons = this.findViewById(R.id.container_login_buttons)
        loginButtonKakao = this.findViewById(R.id.login_button_kakao)

        val loginAnimation = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(logo, "translationY", -300f).apply {
                startDelay = 1000
                duration = 1500
            }).before(ObjectAnimator.ofFloat(containerLoginButtons, View.ALPHA,  1f).apply {
                startDelay = 500
                duration = 500
            })
        }

        loginButtonKakao.setOnClickListener {
            KakaoLoginService.login(this,
                onSuccess = { accessToken -> viewModel.onKakaoLoginSuccess(this, accessToken) },
                onFailure = { -> viewModel.onKakaoLoginFailure(this) },
            )
//            val kakaoService = KakaoService.getInstance(this.applicationContext)
//            kakaoService.login(
//                onSuccess = { accessToken -> viewModel.onKakaoLoginSuccess(this, accessToken) },
//                onFailure = { -> viewModel.onKakaoLoginFailure(this) },
//            )
        }

        if (viewModel.hasCachedToken(this)) {
            Timer().schedule(object: TimerTask() {
                override fun run() {
                    viewModel.moveToMainActivity(this@LoginActivity)
                }
            }, 1000)
        } else {
            loginAnimation.start()
        }
    }



}