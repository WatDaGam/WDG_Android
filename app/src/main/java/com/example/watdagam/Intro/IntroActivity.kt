package com.example.watdagam.Intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.watdagam.R
import java.util.Timer
import java.util.TimerTask

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val model: IntroViewModel by viewModels()
        Timer().schedule(object: TimerTask() {
            override fun run() {
                model.checkToken(this@IntroActivity)
            }
        }, 1000)
    }
}