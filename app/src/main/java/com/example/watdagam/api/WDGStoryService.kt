package com.example.watdagam.api

import android.content.Context
import android.util.Log
import com.example.watdagam.data.LocationDto
import com.example.watdagam.data.PostDto
import com.example.watdagam.data.StoryDto
import com.example.watdagam.data.StoryListDto
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

class WDGStoryService {
    interface StoryApi {
        @POST("story/upload")
        suspend fun uploadStory(
            @Header("Authorization") token: String,
            @Body post: PostDto,
        ): Response<Void>

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
            @Body locationDto: LocationDto,
        ): Response<StoryListDto>

        @POST("like/plus")
        suspend fun likeStory(
            @Header("Authorization") token: String,
            @Query("storyId") storyId: Long,
        ): Response<Void>

        @GET("myStory")
        suspend fun getMyStoryList(
            @Header("Authorization") token: String,
        ): Response<StoryListDto>
    }

    companion object {
        private const val TAG = "WDG_story_service"
        private const val BASE_URL = "http://43.202.3.132:8080"
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val storyApi: StoryApi = retrofit.create(StoryApi::class.java)

        suspend fun uploadStory(
            context: Context,
            content: String,
            latitude: Double,
            longitude: Double,
        ): Response<Void> {
            val accessToken = WDGUserService.getAccessToken(context)
            val story = PostDto(content, latitude, longitude)
            val response = storyApi.uploadStory("Bearer $accessToken", story)
            Log.d(TAG, "Get response story/upload\n" + response.raw().toString())
            if (response.code() == 401) {
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun getStory(
            context: Context,
            storyId: Long,
        ): Response<StoryDto> {
            val accessToken = WDGUserService.getAccessToken(context)
            val response = storyApi.getStory("Bearer $accessToken", storyId)
            Log.d(TAG, "Get response story/get\n" + response.raw().toString())
            if (response.code() == 401) {
                WDGUserService.requestLogin(context)
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun deleteStory(
            context: Context,
            storyId: Long,
        ): Response<Void> {
            val accessToken = WDGUserService.getAccessToken(context)
            val response = storyApi.deleteStory("Bearer $accessToken", storyId)
            Log.d(TAG, "Get response story/delete\n" + response.raw().toString())
            if (response.code() == 401) {
                WDGUserService.requestLogin(context)
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun getStoryList(
            context: Context,
            latitude: Double,
            longitude: Double,
        ): Response<StoryListDto> {
            val accessToken = WDGUserService.getAccessToken(context)
            val locationDto = LocationDto(latitude, longitude)
            val response = storyApi.getStoryList("Bearer $accessToken", locationDto)
            Log.d(TAG, "Get response storyList/renew\n" + response.raw().toString())
            if (response.code() == 401) {
                WDGUserService.requestLogin(context)
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun addLike(
            context: Context,
            storyId: Long,
        ): Response<Void> {
            val accessToken = WDGUserService.getAccessToken(context)
            val response = storyApi.likeStory("Bearer $accessToken", storyId)
            Log.d(TAG, "Get response like/add\n" + response.raw().toString())
            if (response.code() == 401) {
                WDGUserService.requestLogin(context)
                throw Exception("Not Valid Token")
            }
            return response
        }

        suspend fun getMyStoryList(
            context: Context,
        ): Response<StoryListDto> {
            val accessToken = WDGUserService.getAccessToken(context)
            val response = storyApi.getMyStoryList("Bearer $accessToken")
            Log.d(TAG, "Get response myStory\n" + response.raw().toString())
            if (response.code() == 401) {
                WDGUserService.requestLogin(context)
                throw Exception("Not Valid Token")
            }
            return response
        }
    }
}
