package com.example.watdagam.Signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.example.watdagam.R

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
                .setPositiveButton("네") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val model: SignupViewModel by viewModels()
        model.getNicknameStatus().observe(this) { status ->
            when (status) {
                unchecked -> {
                    notiText.setCompoundDrawablesRelative(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.icon_info
                        ), null, null, null
                    )
                    notiText.setText(R.string.nickname_noti_info)
                    notiText.setTextColor(getColor(R.color.gray))
                    buttonSubmit.setText(R.string.nickname_button_check)
                }

                valid -> {
                    notiText.setCompoundDrawablesRelative(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.icon_check_circle
                        ), null, null, null
                    )
                    notiText.setText(R.string.nickname_noti_valid)
                    notiText.setTextColor(getColor(R.color.green))
                    buttonSubmit.setText(R.string.nickname_button_signup)
                }

                invalid -> {
                    notiText.setCompoundDrawablesRelative(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.icon_x_circle
                        ), null, null, null
                    )
                    notiText.setText(R.string.nickname_noti_invalid)
                    notiText.setTextColor(getColor(R.color.red))
                    buttonSubmit.setText(R.string.nickname_button_check)
                }
            }
        }

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
                model.resetValidity()
            }
        })

        buttonCancel.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        buttonSubmit.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            if (model.getNicknameStatus().value == valid) {
                model.setNickname(nickname, this)
            } else {
                model.checkNickname(nickname, this)
            }
        }

    }
}