package com.example.mystory.api

import com.example.mystory.*
import com.example.mystory.datamodel.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token : String,
        @Query("page") page: Int,
        @Query("size") size: Int): GetStories

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getAllPagingStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoryResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part("description") des: String,
        @Part file: MultipartBody.Part
    ): Call<ApiResponse>

//    https://story-api.dicoding.dev/v1/stories?location=1
    @GET("stories")
    fun getAllLocationStories(
        @Header("Authorization") token: String,
        @Query("location") location: String
    ): Call<StoryResponse>

}
