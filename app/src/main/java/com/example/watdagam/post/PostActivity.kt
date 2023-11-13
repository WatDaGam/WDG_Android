package com.example.watdagam.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.watdagam.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPostBinding
    private val model: PostActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

//        model.getPostAddress().observe(requireActivity()) { address: Address ->
//            viewBinding.locationName.text =
//                if (!address.thoroughfare.isNullOrBlank()) {
//                    address.thoroughfare
//                } else if (!address.subLocality.isNullOrBlank()) {
//                    address.subLocality
//                } else if (!address.locality.isNullOrBlank()) {
//                    address.locality
//                } else if (!address.subAdminArea.isNullOrBlank()) {
//                    address.subAdminArea
//                } else if (!address.adminArea.isNullOrBlank()) {
//                    address.adminArea
//                } else {
//                    address.countryName
//                }
//        }

        viewBinding.textEdit.addTextChangedListener { editable: Editable? ->
            if (editable != null) {
                viewBinding.textCount.text = "${editable.length}/100"
            }
        }
        viewBinding.textCount.text = "0/100"

        viewBinding.post.setOnClickListener {
            if (viewBinding.textEdit.text.isNullOrBlank()) {
                Toast.makeText(this, "남길 내용이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, viewBinding.textEdit.text.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.toolbarBack.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

//        this.onBackPressedDispatcher.addCallback(this, backPressCallback)
    }
}