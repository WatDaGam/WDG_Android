package com.example.watdagam.login

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import com.example.watdagam.R
import com.example.watdagam.utils.KakaoLoginService
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
        }

        TedPermission.create()
            .setPermissionListener(object: PermissionListener {
                override fun onPermissionGranted() {
                    if (viewModel.hasCachedToken(this@LoginActivity)) {
                        Timer().schedule(object: TimerTask() {
                            override fun run() {
                                viewModel.moveToMainActivity(this@LoginActivity)
                            }
                        }, 1000)
                    } else {
                        loginAnimation.start()
                    }
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@LoginActivity, "정확한 위치 사용을 허용하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
            .setDeniedMessage("정확한 위치 권한이 필요합니다.")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .check()

    }



}