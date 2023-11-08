package com.example.watdagam.Signup

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.MainActivity
import com.example.watdagam.api.ApiService
import kotlinx.coroutines.launch
import java.util.regex.Pattern

const val unchecked = "NICKNAME_UNCHECKED"
const val valid = "NICKNAME_VALID"
const val invalid = "NICKNAME_INVALID"
class SignupViewModel: ViewModel() {

    private val nicknameStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            unchecked
        }
    }
    @JvmName("callFromString")
    fun getNicknameStatus(): MutableLiveData<String> {
        return nicknameStatus
    }

    fun resetValidity() {
        nicknameStatus.value = unchecked
    }

    fun checkNickname(nickname: String, context: Context) {
        viewModelScope.launch {
            if (nickname.length < 2 || nickname.length > 10) {
                Toast.makeText(context, "2자 이상 10자 이하만 가능합니다.", Toast.LENGTH_SHORT).show()
                nicknameStatus.value = invalid
            } else if (!Pattern.matches("^[a-zA-Z0-9가-힣]*$", nickname)) {
                Toast.makeText(context, "완성되지 않은 한글 및 특수문자는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                nicknameStatus.value = invalid
            } else {
                val apiService = ApiService.getInstance(context)
                apiService.checkNickname(
                    nickname,
                    onSuccess = { _, response ->
                        if (response.isSuccessful) {
                            nicknameStatus.value = valid
                        } else {
                            nicknameStatus.value = invalid
                        }
                    },
                    onFailure = { _, _ -> null },
                )
            }
        }
    }

    fun setNickname(nickname: String, context: Context) {
        viewModelScope.launch {
            val apiService = ApiService.getInstance(context)
            apiService.setNickname(
                nickname,
                onSuccess = { _, response ->
                    if (response.isSuccessful) {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        nicknameStatus.value = invalid
                    }
                },
                onFailure = { _, _ -> null },
            )
        }
    }
}