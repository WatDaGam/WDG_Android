package com.example.watdagam.api

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class ApiService private constructor() {
    companion object {
        private var instance: ApiService? = null
        private lateinit var token_pref: TokenSharedPreference

        private const val BASE_URL: String = "https://0b88436b-a8a0-463a-bb4d-07b31d747be2.mock.pstmn.io"
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val apiService: WDGApiService = retrofit.create(WDGApiService::class.java)

        fun getInstance(context: Context): ApiService {
            return instance ?: ApiService().also {
                token_pref = TokenSharedPreference(context)
                instance = it
                Log.d("WDG", "initialized")
            }
        }

    }

    interface WDGApiService {
        @GET("login")
        fun login(
            @Query("platform") platform: String,
            @Header("Authorization") token: String,
        ): Call<Void>

//        @GET("refreshAccessToken")
//        fun refreshAccessToken(
//            @Header("Authorization") token: String,
//        ): Call<Void>
//
//        @POST("checkNickname")
//        fun checkNickname(
//            @Header("Authorization") token: String,
//            @Body nickname: String,
//        ): Call<Void>
//
//        @POST("setNickname")
//        fun setNickname(
//            @Header("Authorization") token: String,
//            @Body nickname: String,
//        ): Call<Void>
//
//        @DELETE("withdrawal")
//        fun withdrawal(
//            @Header("Authorization") token: String,
//        ): Call<Void>
    }

     fun login(
        platform: String,
        platformToken: String,
        callback: (Call<Void>, Response<Void>) -> Unit,
    ) {
        val response = apiService.login(
            platform,
            "Bearer $platformToken",
        )
        response.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    token_pref.accessToken = response.headers()["accessToken"]
                    token_pref.refreshToken = response.headers()["refreshToken"]
                }
                callback(call, response)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}