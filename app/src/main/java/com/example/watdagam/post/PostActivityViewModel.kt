package com.example.watdagam.post

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watdagam.api.ApiService
import kotlinx.coroutines.launch

class PostActivityViewModel: ViewModel() {
   private val TAG = "WDG_POST_ACTIVITY"
    fun postStory(context: Context, content: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val apiService = ApiService.getInstance(context)
                apiService.uploadStory(context, content, latitude, longitude)
                Toast.makeText(context, "메세지를 남겼습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: RuntimeException) {
                Log.e(TAG, e.message ?: "no error message")
            }
        }
    }
}