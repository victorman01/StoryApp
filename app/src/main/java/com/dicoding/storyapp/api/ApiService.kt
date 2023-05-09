package com.dicoding.storyapp.api

import com.dicoding.storyapp.response.AddNewStoryResponse
import com.dicoding.storyapp.response.LoginResponse
import com.dicoding.storyapp.response.RegisterResponse
import com.dicoding.storyapp.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoriesResponse>

    @GET("stories")
    fun getAllStoriesLoc(
        @Header("Authorization") token: String,
        @Query("location") loc: Int = 1
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lat") page: Double?,
        @Part("lon") size: Double?
    ): Call<AddNewStoryResponse>
}