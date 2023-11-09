package com.example.watdagam.Intro

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.LoginActivity
import com.example.watdagam.MainActivity
import com.example.watdagam.api.ApiService
import kotlinx.coroutines.launch

class IntroViewModel: ViewModel() {
    fun checkToken(context: Context) {
        viewModelScope.launch {
            val apiService = ApiService.getInstance(context.applicationContext)
            if (ApiService.token_pref.getAccessToken() == "" &&
                ApiService.token_pref.getRefreshToken() == "") {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            } else {
                try {
                    apiService.getAccessToken(context)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } catch (e: RuntimeException) {
                    Log.e("WDG_INTRO", e.message ?: "(no error message)")
                }
            }
        }
    }
}