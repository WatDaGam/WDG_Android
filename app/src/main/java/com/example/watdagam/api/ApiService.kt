package com.example.watdagam.api

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.watdagam.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

class ApiService private constructor() {
    companion object {
        private var instance: ApiService? = null
        private lateinit var appContext: Context
        lateinit var token_pref: TokenSharedPreference
        lateinit var user_data_pref: UserDataSharedPreference

        private const val TAG = "WDG_API"
        private const val BASE_URL: String = "http://52.78.126.48:8080"
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val loginService: LoginService = retrofit.create(LoginService::class.java)
        private val userService: UserService = retrofit.create(UserService::class.java)

        fun getInstance(context: Context): ApiService {
            return instance ?: ApiService().also {
                appContext = context.applicationContext
                token_pref = TokenSharedPreference(appContext)
                user_data_pref = UserDataSharedPreference(appContext)
                instance = it
            }
        }
    }

    interface LoginService {
        @GET("login")
        fun login(
            @Query("platform") platform: String,
            @Header("Authorization") token: String,
        ): Call<Void>

        @GET("refreshtoken")
        suspend fun refreshToken(
            @Header("Authorization") token: String,
        ): Response<Void>

        @GET("withdrawal")
        fun withdrawal(
            @Header("Authorization") token: String,
        ): Call<Void>
    }

    private fun requestLogin() {
        val intent = Intent(appContext, LoginActivity::class.java)
        val builder: AlertDialog.Builder = AlertDialog.Builder(appContext)
        builder
            .setMessage("로그인 정보가 만료되었습니다. 다시 로그인해주세요")
        val dialog: AlertDialog = builder.create()
        dialog.show()
        appContext.startActivity(intent)
    }

     fun login(
         platform: String,
         platformToken: String,
         onSuccess: (Call<Void>, Response<Void>) -> Unit,
         onFailure: (Call<Void>, Throwable) -> Unit,
    ) {
        val request = loginService.login(
            platform,
            "Bearer $platformToken",
        )
         request.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "요청 성공 (login) ${response.code()} ${response.message()}")
                if (response.isSuccessful) {
                    val accessToken = response.headers()["Authorization"].toString().split(" ")[1]
                    val refreshToken = response.headers()["Refresh-Token"].toString()
                    val accessTokenExpTime = response.headers()["access-expiration-time"].toString().toLong()
                    val refreshTokenExpTime = response.headers()["refresh-expiration-time"].toString().toLong()
                    token_pref.setAccessToken(accessToken, accessTokenExpTime)
                    token_pref.setRefreshToken(refreshToken, refreshTokenExpTime)
                }
                onSuccess(call, response)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "요청 실패 (login)")
                onFailure(call, t)
            }
        })
    }

    suspend fun getAccessToken(): Result<String> = kotlin.runCatching {
        val cachedAccessToken = token_pref.getAccessToken()
        if (cachedAccessToken.isNotEmpty()) {
            cachedAccessToken
        } else {
            val cachedRefreshToken = token_pref.getRefreshToken()
            if (cachedRefreshToken.isEmpty()) {
                requestLogin()
                throw RuntimeException("login required")
            }
            val response = loginService.refreshToken("Bearer $cachedRefreshToken")
            when (response.code()) {
                200 -> {
                    val accessToken = response.headers()["Authorization"].toString().split(" ")[1]
                    val accessTokenExpTime =
                        response.headers()["access-expiration-time"].toString().toLong()
                    token_pref.setAccessToken(accessToken, accessTokenExpTime)
                    accessToken
                }

                401 -> {
                    requestLogin()
                    throw RuntimeException("login required")
                }

                else -> {
                    Log.e(TAG, "Unhandled response code (refreshtoken) ${response.code()}")
                    Log.e(TAG, response.toString())
                    throw RuntimeException("unhandled response code")
                }
            }
        }
    }

    suspend fun withdrawal(
        onSuccess: (Call<Void>, Response<Void>) -> Unit,
        onFailure: (Call<Void>, Throwable) -> Unit,
    ) {
        val accessToken = getAccessToken()
        val request = loginService.withdrawal("Bearer $accessToken")
        request.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "요청 성공 (withdrawal) ${response.code()} ${response.message()}")
                if (response.isSuccessful) {
                    token_pref.setAccessToken("", 0)
                    token_pref.setRefreshToken("", 0)
                }
                onSuccess(call, response)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "요청 실패 (withdrawal)")
                onFailure(call, t)
            }
        })
    }


    interface UserService {
        @POST("nickname/check")
        fun checkNickname(
            @Header("Authorization") token: String,
            @Body nickname: String,
        ): Call<Void>

        @POST("nickname/set")
        fun setNickname(
            @Header("Authorization") token: String,
            @Body nickname: String,
        ): Call<Void>
    }

    suspend fun checkNickname(
        nickname: String,
        onSuccess: (Call<Void>, Response<Void>) -> Unit,
        onFailure: (Call<Void>, Throwable) -> Unit,
    ) {
        getAccessToken()
            .onSuccess {accessToken ->
                val request = userService.checkNickname("Bearer $accessToken", nickname)
                request.enqueue(object: Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d(TAG, "요청 성공 (nickname/check) ${response.code()} ${response.message()}")
                        if (response.code() == 401) {
                            requestLogin()
                        } else {
                            onSuccess(call, response)
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e(TAG, "요청 실패 (nickname/check)")
                        onFailure(call, t)
                    }
                })
            }
    }

    suspend fun setNickname(
        nickname: String,
        onSuccess: (Call<Void>, Response<Void>) -> Unit,
        onFailure: (Call<Void>, Throwable) -> Unit,
    ) {
        getAccessToken()
            .onSuccess { accessToken ->
                val request = userService.setNickname("Bearer $accessToken", nickname)
                request.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d(TAG, "요청 성공 (nickname/set) ${response.code()} ${response.message()}")
                        if (response.code() == 401) {
                            requestLogin()
                        }
                        if (response.isSuccessful) {
                            val newAccessToken =
                                response.headers()["Authorization"].toString().split(" ")[1]
                            val newRefreshToken = response.headers()["Refresh-Token"].toString()
                            val newAccessTokenExpTime =
                                response.headers()["access-expiration-time"].toString().toLong()
                            val newRefreshTokenExpTime =
                                response.headers()["refresh-expiration-time"].toString().toLong()
                            token_pref.setAccessToken(newAccessToken, newAccessTokenExpTime)
                            token_pref.setRefreshToken(newRefreshToken, newRefreshTokenExpTime)
                        }
                        onSuccess(call, response)
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e(TAG, "요청 실패 (nickname/set)")
                        onFailure(call, t)
                    }
                })
            }
    }


}