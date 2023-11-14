package com.example.watdagam.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.watdagam.R
import com.example.watdagam.api.ApiService
import com.example.watdagam.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPostBinding
    private val model: PostActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val address = intent.extras?.getString("KEY_WDG_ADDRESS") ?: ""
        val latitude = intent.extras?.getDouble("KEY_WDG_LATITUDE") ?: 0.0
        val longitude = intent.extras?.getDouble("KEY_WDG_LONGITUDE") ?: 0.0
        val nickname = ApiService.user_data_pref.nickname ?: ""
        viewBinding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarTitle.text = getString(R.string.toolbar_title_post, nickname)
        viewBinding.locationName.text = address

        viewBinding.textEdit.addTextChangedListener { editable: Editable? ->
            if (editable != null) {
                viewBinding.textCount.text = getString(R.string.post_text_count, editable.length)
            }
        }
        viewBinding.textCount.text = getString(R.string.post_text_count, 0)

        viewBinding.post.setOnClickListener {
            if (viewBinding.textEdit.text.isNullOrBlank()) {
                Toast.makeText(this, "남길 내용이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, viewBinding.textEdit.text.toString(), Toast.LENGTH_SHORT).show()
                model.postStory(this.applicationContext, viewBinding.textEdit.text.toString(), latitude, longitude)
            }
        }

        viewBinding.toolbarBack.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        this.onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    private val backPressCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!viewBinding.textEdit.text.isNullOrBlank()) {
                val dialog = AlertDialog.Builder(this@PostActivity)
                    .setMessage("메세지가 사라집니다. 그래도 뒤로 가시겠습니까?")
                    .setNegativeButton("아니요", null)
                    .setPositiveButton("네"){ _, _ -> finish()}
                    .create()
                dialog.show()
            } else {
                finish()
            }
        }
    }
}