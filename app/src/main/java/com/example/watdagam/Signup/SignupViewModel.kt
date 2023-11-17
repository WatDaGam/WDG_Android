package com.example.watdagam.Signup

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGUserService
import com.example.watdagam.main.MainActivity
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
            try {
                if (nickname.length < 2 || nickname.length > 10) {
                    Toast.makeText(context, "2자 이상 10자 이하만 가능합니다.", Toast.LENGTH_SHORT).show()
                    nicknameStatus.value = invalid
                } else if (!Pattern.matches("^[a-zA-Z0-9가-힣]*$", nickname)) {
                    Toast.makeText(context, "완성되지 않은 한글 및 특수문자는 사용할 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                    nicknameStatus.value = invalid
                } else {
                    val response = WDGUserService.checkNickname(context, nickname)
                    if (response.isSuccessful) {
                        nicknameStatus.value = valid
                    } else {
                        nicknameStatus.value = invalid
                        Toast.makeText(context, "중복된 닉네임 입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("WDG_signup_activity", e.toString())
            }
        }
    }

    fun setNickname(nickname: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = WDGUserService.setNickname(context, nickname)
                if (response.isSuccessful) {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    nicknameStatus.value = invalid
                    Toast.makeText(context, "앗! 고민하는 사이 누군가 닉네임을 선점했어요.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("WDG_signup_activity", e.toString())
            }
        }
    }
}