package com.example.watdagam

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout

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

        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(logo, "translationY", -300f).apply {
                startDelay = 1000
                duration = 1500
            }).before(ObjectAnimator.ofFloat(containerLoginButtons, View.ALPHA, 0f, 1f).apply {
                startDelay = 500
                duration = 500
            })
        }.start()
    }
}