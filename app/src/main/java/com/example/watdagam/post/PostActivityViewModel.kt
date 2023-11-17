package com.example.watdagam.post

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.WDGStoryService
import kotlinx.coroutines.launch

class PostActivityViewModel: ViewModel() {
   private val TAG = "WDG_POST_ACTIVITY"
    fun postStory(context: Context, content: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = WDGStoryService.uploadStory(context, content, latitude, longitude)
                if (response.isSuccessful) {
                    Toast.makeText(context, "메세지를 남겼습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: RuntimeException) {
                Log.e(TAG, e.message ?: "no error message")
            }
        }
    }
}