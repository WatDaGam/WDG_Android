package com.example.watdagam.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.login.LoginActivity
import com.example.watdagam.utils.storage.StorageService
import kotlinx.coroutines.launch

class MyPageViewModel: ViewModel() {
    companion object {
        private const val TAG = "WDG_myPageViewModel"
    }

    fun logout(context: Context) {
        val tokenService = StorageService.getInstance(context).getTokenService()
        tokenService.setAccessToken("", 0)
        tokenService.setRefreshToken("", 0)
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
    }

    fun withdraw(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setMessage("회원탈퇴 하시겠습니까?")
            .setNegativeButton("아니요", null)
            .setPositiveButton("네") {_, _ ->
                viewModelScope.launch {
                    try {
                        WDGUserService.withdraw(context)
                        Toast.makeText(context, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: RuntimeException) {
                        Log.e(TAG, e.message ?: "(no error message)")
                    }
                }
            }
            .create()
        dialog.show()
    }
}