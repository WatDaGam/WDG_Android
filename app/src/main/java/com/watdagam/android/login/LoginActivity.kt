package com.watdagam.android.login

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import com.watdagam.android.R
import com.watdagam.android.utils.KakaoLoginService
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var loginButtonKakao: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        KakaoLoginService.initializeSdk(this)

        loginButtonKakao = this.findViewById(R.id.login_button_kakao)
        loginButtonKakao.setOnClickListener {
            viewModel.kakaoLogin(this)
        }

        TedPermission.create()
            .setPermissionListener(object: PermissionListener {
                override fun onPermissionGranted() {
                    viewModel.startLocationTracking(this@LoginActivity)
                    viewModel.loadUserData(this@LoginActivity)
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