package com.watdagam.android.utils.api

import android.content.Context
import android.util.Log
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

object StoryService {
    interface StoryApi {
        @POST("story/upload")
        suspend fun uploadStory(
            @Header("Authorization") token: String,
            @Body post: StoryUploadBodyDto,
        ): Response<StoryDto>

        @GET("story/info")
        suspend fun getStory(
            @Header("Authorization") token: String,
            @Query("storyId") storyId: Long,
        ): Response<StoryDto>

        @DELETE("story/delete")
        suspend fun deleteStory(
            @Header("Authorization") token: String,
            @Query("storyId") storyId: Long,
        ): Response<Void>

        @POST("storyList/renew")
        suspend fun getStoryList(
            @Header("Authorization") token: String,
            @Body locationDto: StoryListRenewBodyDto,
        ): Response<StoryListDto>

        @POST("like/plus")
        suspend fun likeStory(
            @Header("Authorization") token: String,
            @Query("storyId") storyId: Long,
        ): Response<AddLikeDto>

        @GET("myStory")
        suspend fun getMyStoryList(
            @Header("Authorization") token: String,
        ): Response<StoryListDto>

        @GET("report")
        suspend fun reportStory(
            @Header("Authorization") token: String,
            @Query("storyId") storyId: Long,
        ): Response<Void>
    }

    data class AddLikeDto(
        val likeNum: Int
    )

    data class StoryDto(
        val createdAt: String,
        val lati: Double,
        val longi: Double,
        val nickname: String,
        val id: Long,
        val userId: Int,
        val content: String,
        val likeNum: Int
    )

    data class StoryListDto(
        val stories: List<StoryDto>
    )

    data class StoryUploadBodyDto(
        val content: String,
        val lati: Double,
        val longi: Double
    )

    data class StoryListRenewBodyDto(
        val lati: Double,
        val longi: Double
    )

    private const val TAG = "WDG_story_service"
    private val retrofit = Retrofit.Builder()
        .baseUrl(WDGApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val storyApi: StoryApi = retrofit.create(StoryApi::class.java)

    suspend fun uploadStory(
        context: Context,
        content: String,
        latitude: Double,
        longitude: Double,
    ): Response<StoryDto> {
        val accessToken = UserService.getAccessToken(context)
        val story = StoryUploadBodyDto(content, latitude, longitude)
        val response = storyApi.uploadStory("Bearer $accessToken", story)
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            throw Exception("Not Valid Token")
        }
        return response
    }

    suspend fun getStory(
        context: Context,
        storyId: Long,
    ): Response<StoryDto> {
        val accessToken = UserService.getAccessToken(context)
        val response = storyApi.getStory("Bearer $accessToken", storyId)
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            UserService.requestLogin(context)
            throw Exception("Not Valid Token")
        }
        return response
    }

    suspend fun deleteStory(
        context: Context,
        storyId: Long,
    ): Response<Void> {
        val accessToken = UserService.getAccessToken(context)
        val response = storyApi.deleteStory("Bearer $accessToken", storyId)
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            UserService.requestLogin(context)
            throw Exception("Not Valid Token")
        }
        return response
    }

    suspend fun getStoryList(
        context: Context,
        latitude: Double,
        longitude: Double,
    ): Response<StoryListDto> {
        val accessToken = UserService.getAccessToken(context)
        val locationDto = StoryListRenewBodyDto(latitude, longitude)
        val response = storyApi.getStoryList("Bearer $accessToken", locationDto)
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            UserService.requestLogin(context)
            throw Exception("Not Valid Token")
        }
        return response
    }

    suspend fun addLike(
        context: Context,
        storyId: Long,
    ): Response<AddLikeDto> {
        val accessToken = UserService.getAccessToken(context)
        val response = storyApi.likeStory("Bearer $accessToken", storyId)
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            UserService.requestLogin(context)
            throw Exception("Not Valid Token")
        }
        return response
    }

    suspend fun getMyStoryList(
        context: Context,
    ): Response<StoryListDto> {
        val accessToken = UserService.getAccessToken(context)
        val response = storyApi.getMyStoryList("Bearer $accessToken")
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            UserService.requestLogin(context)
            throw Exception("Not Valid Token")
        }
        return response
    }

    suspend fun reportStory(
        context: Context,
        storyId: Long,
    ): Response<Void> {
        val accessToken = UserService.getAccessToken(context)
        val response = storyApi.reportStory("Bearer $accessToken", storyId)
        Log.d(TAG, "${response.raw()} ${response.headers()} ${response.body()}")
        if (response.code() == 401) {
            UserService.requestLogin(context)
            throw Exception("Not Valid Token")
        }
        return response
    }
}