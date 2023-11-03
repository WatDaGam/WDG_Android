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
        private lateinit var appContext: Context
        private lateinit var token_pref: TokenSharedPreference

        private const val TAG = "WDG_API"
        private const val BASE_URL: String = "http://52.78.126.48:8080"
//        private const val BASE_URL: String = "https://0b88436b-a8a0-463a-bb4d-07b31d747be2.mock.pstmn.io"
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val apiService: WDGApiService = retrofit.create(WDGApiService::class.java)

        fun getInstance(context: Context): ApiService {
            return instance ?: ApiService().also {
                appContext = context.applicationContext
                token_pref = TokenSharedPreference(appContext)
                instance = it
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
         onSuccess: (Call<Void>, Response<Void>) -> Unit,
         onFailure: (Call<Void>, Throwable) -> Unit,
    ) {
        val response = apiService.login(
            platform,
            "Bearer $platformToken",
        )
        response.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "로그인 요청 성공: ${response.code()} ${response.message()}")
                if (response.isSuccessful) {
                    val accessToken = response.headers()["Authorization"].toString().split(" ")[1]
                    val refreshToken = response.headers()["Refresh-Token"].toString()
                    val accessTokenExpTime = response.headers()["access-expiration-time"].toString().toLong()
                    val refreshTokenExpTime = response.headers()["refresh-expiration-time"].toString().toLong()
                    Log.d(TAG, "access token: $accessToken expire in $accessTokenExpTime")
                    Log.d(TAG, "refresh token: $refreshToken expire in $refreshTokenExpTime")
                    token_pref.accessToken = accessToken
                    token_pref.accessTokenExpirationTime = accessTokenExpTime
                    token_pref.refreshToken = refreshToken
                    token_pref.refreshTokenExpirationTime = refreshTokenExpTime
                }
                onSuccess(call, response)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "로그인 요청 실패")
                onFailure(call, t)
            }
        })
    }
}