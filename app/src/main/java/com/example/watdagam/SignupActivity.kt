package com.example.watdagam

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.example.watdagam.api.ApiService
import retrofit2.Response
import java.util.regex.Pattern

class SignupActivity: AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var notiText: TextView
    private lateinit var buttonCancel: AppCompatButton
    private lateinit var buttonSubmit: AppCompatButton
    private var checked: Boolean = false

    private var backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val builder = AlertDialog.Builder(this@SignupActivity)
                .setMessage("회원가입 정보가 삭제됩니다. 그래도 뒤로 가시겠습니까?")
                .setPositiveButton("네") {_, _ -> finish()}
                .setNegativeButton("아니오", null)
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)

        nicknameEditText = this.findViewById(R.id.nickname)
        notiText = this.findViewById(R.id.nickname_noti_text)
        buttonCancel = this.findViewById(R.id.button_cancel)
        buttonSubmit = this.findViewById(R.id.button_submit)
        checked = false

        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setStatusInfo()
            }
        })

        buttonCancel.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        buttonSubmit.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            if (!checked) {
                if (nickname.length < 2 || nickname.length > 10) {
                    Toast.makeText(this, "2자 이상 10자 이하만 가능합니다.", Toast.LENGTH_SHORT).show()
                    setStatusInvalid()

                } else if (!Pattern.matches("^[a-zA-Z0-9가-힣]*$", nickname)) {
                    Toast.makeText(this, "완성되지 않은 한글 및 특수문자는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    setStatusInvalid()
                } else {
                    // checkNickname
                    val apiService = ApiService.getInstance(this)
                    apiService.checkNickname(
                        nickname,
                        onSuccess = {_, response -> onCheckNicknameSuccess(response)},
                        onFailure = {_, _ -> onCheckNicknameFailure()}
                    )
                }
            } else {
                val apiService = ApiService.getInstance(this)
                apiService.setNickname(
                    nickname,
                    onSuccess = {_, response -> onSetNicknameSuccess(response)},
                    onFailure = {_, _ -> onSetNicknameFailure()},
                )
            }
        }

    }

//    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
//        val builder = AlertDialog.Builder(this)
//        builder
//            .setTitle("페이지를 벗어나면 회원가입이 취소됩니다.")
//            .setMessage("뒤로가시겠습니끼?")
//            .setPositiveButton("네") { _, _ ->
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            }
//            .setNegativeButton("아니요", null)
//        val dialog = builder.create()
//        dialog.show()
//        return super.getOnBackInvokedDispatcher()
//    }

    private fun setStatusInfo() {
        checked = false
        notiText.setCompoundDrawablesRelative(AppCompatResources.getDrawable(this, R.drawable.icon_info), null, null, null)
        notiText.setText(R.string.nickname_noti_info)
        notiText.setTextColor(getColor(R.color.gray))
        buttonSubmit.setText(R.string.nickname_button_check)
    }

    private fun setStatusValid() {
        checked = true
        notiText.setCompoundDrawablesRelative(AppCompatResources.getDrawable(this, R.drawable.icon_check_circle), null, null, null)
        notiText.setText(R.string.nickname_noti_valid)
        notiText.setTextColor(getColor(R.color.green))
        buttonSubmit.setText(R.string.nickname_button_signup)
    }

    private fun setStatusInvalid() {
        checked = false
        notiText.setCompoundDrawablesRelative(AppCompatResources.getDrawable(this, R.drawable.icon_x_circle), null, null, null)
        notiText.setText(R.string.nickname_noti_invalid)
        notiText.setTextColor(getColor(R.color.red))
        buttonSubmit.setText(R.string.nickname_button_check)
    }

    private fun onCheckNicknameSuccess(response: Response<Void>) {
        when (response.code()) {
            200 -> {
                setStatusValid()
            }
            400 -> {
                Toast.makeText(this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show()
                setStatusInvalid()
            }
            else -> {
                Log.e("SIGNUP", "unhandled error")
            }
        }
    }

    private fun onCheckNicknameFailure() {
        Toast.makeText(this, "요청에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
    }

    private fun onSetNicknameSuccess(response: Response<Void>) {
        when (response.code()) {
            200 -> {
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            }
            400 -> {
                Toast.makeText(this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show()
                setStatusInvalid()
            }
            else -> {
                Log.e("SIGNUP", "unhandled error")
            }
        }
    }

    private fun onSetNicknameFailure() {
        Toast.makeText(this, "요청에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
    }
}