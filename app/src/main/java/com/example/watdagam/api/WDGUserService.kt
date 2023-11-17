package com.example.watdagam.api

import android.content.Context
import android.util.Log
import com.example.watdagam.data.UserInfo
import com.example.watdagam.storage.StorageService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.lang.Exception

class WDGUserService {
    interface UserApi {
        @GET("login")
        suspend fun login(
            @Query("platform") platform: String,
            @Header("Authorization") token: String,
        ): Response<Void>

        @GET("refreshtoken")
        suspend fun refreshToken(
            @Header("Refresh-Token") token: String,
        ): Response<Void>

        @DELETE("withdrawal")
        suspend fun withdrawal(
            @Header("Authorization") token: String,
        ): Response<Void>
        @POST("nickname/check")
        suspend fun checkNickname(
            @Header("Authorization") token: String,
            @Body nickname: String,
        ): Response<Void>

        @POST("nickname/set")
        suspend fun setNickname(
            @Header("Authorization") token: String,
            @Body nickname: String,
        ): Response<Void>

        @GET("userInfo")
        suspend fun userinfo(
            @Header("Authorization") token: String,
        ): Response<UserInfo>
    }

    companion object {
        private const val TAG = "WDG_user_service"
        private const val BASE_URL = "http://43.202.3.132:8080"
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val userApi: UserApi = retrofit.create(UserApi::class.java)

        private const val KEY_ACCESS_TOKEN = "Authorization"
        private const val KEY_ACCESS_EXPIRATION = "access-expiration-time"
        private const val KEY_REFRESH_TOKEN = "Refresh-Token"
        private const val KEY_REFRESH_EXPIRATION = "refresh-expiration-time"

        suspend fun login(
            context: Context,
            platform: String,
            platformToken: String,
        ): Response<Void> {
            val response = userApi.login(platform, "Bearer $platformToken")
            Log.d(TAG, "Get response login\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            if (response.headers()[KEY_ACCESS_TOKEN].toString().indexOf(" ") == -1) {
                throw Exception("No Access token")
            }
            val tokenService = StorageService.getInstance(context).getTokenService()
            val accessToken = response.headers()[KEY_ACCESS_TOKEN].toString().split(" ")[1]
            val accessExpiration = response.headers()[KEY_ACCESS_EXPIRATION].toString().toLong()
            val refreshToken = response.headers()[KEY_REFRESH_TOKEN].toString()
            val refreshExpiration = response.headers()[KEY_REFRESH_EXPIRATION].toString().toLong()
            tokenService.setAccessToken(accessToken, accessExpiration)
            tokenService.setRefreshToken(refreshToken, refreshExpiration)
            return response
        }

        suspend fun getAccessToken(context: Context): String {
            val tokenService = StorageService.getInstance(context).getTokenService()
            val cachedToken = tokenService.getAccessToken()
            if (cachedToken.isNotEmpty()) {
                return cachedToken
            }
            val refreshToken = tokenService.getRefreshToken()
            if (refreshToken.isEmpty()) {
                throw Exception("Not Valid Token")
            }
            val response = userApi.refreshToken(refreshToken)
            Log.d(TAG, "Get response refreshtoken\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            if (response.headers()[KEY_ACCESS_TOKEN].toString().indexOf(" ") == -1) {
                throw Exception("No Access token")
            }
            val accessToken = response.headers()[KEY_ACCESS_TOKEN].toString().split(" ")[1]
            val accessExpiration = response.headers()[KEY_ACCESS_EXPIRATION].toString().toLong()
            tokenService.setAccessToken(accessToken, accessExpiration)
            return accessToken
        }

        suspend fun withdraw(
            context: Context
        ): Response<Void> {
            val accessToken = getAccessToken(context)
            val response = userApi.withdrawal("Bearer $accessToken")
            Log.d(TAG, "Get response withdrawal\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            return response
        }
        suspend fun checkNickname(
            context: Context,
            nickname: String,
        ): Response<Void> {
            val accessToken = getAccessToken(context)
            val response = userApi.checkNickname("Bearer $accessToken", nickname)
            Log.d(TAG, "Get response nickname/check\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun setNickname(
            context: Context,
            nickname: String,
        ): Response<Void> {
            val accessToken = getAccessToken(context)
            val response = userApi.setNickname("Bearer $accessToken", nickname)
            Log.d(TAG, "Get response nickname/set\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun getUserInfo(
            context: Context
        ): Response<UserInfo> {
            val accessToken = getAccessToken(context)
            val response = userApi.userinfo("Bearer $accessToken")
            Log.d(TAG, "Get response userinfo\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            return response
        }
    }
}