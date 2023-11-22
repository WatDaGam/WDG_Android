package com.example.watdagam.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.watdagam.R
import com.example.watdagam.databinding.ActivityPostBinding
import com.example.watdagam.utils.storage.StorageService

class PostActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPostBinding
    private val model: PostActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nickname = StorageService.getInstance(this).getProfileService().nickname
        viewBinding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarTitle.text = getString(R.string.toolbar_title_post, nickname)
        viewBinding.textCount.text = getString(R.string.post_text_count, 0)
        model.getAddress().observe(this) { address ->
            viewBinding.locationName.text = address.featureName
        }
        viewBinding.textEdit.addTextChangedListener { editable: Editable? ->
            if (editable != null) {
                viewBinding.textCount.text = getString(R.string.post_text_count, editable.length)
            }
        }
        viewBinding.post.setOnClickListener {
            model.postStory(this, viewBinding.textEdit.text.toString())
        }
        viewBinding.toolbarBack.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        model.updateAddress(this)
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