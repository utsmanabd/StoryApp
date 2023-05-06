package com.everybodv.storyapp.data.remote.retrofit

import com.everybodv.storyapp.data.remote.response.LoginResponse
import com.everybodv.storyapp.data.remote.response.RegisterResponse
import com.everybodv.storyapp.data.remote.response.StoriesResponse
import com.everybodv.storyapp.data.remote.response.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): StoriesResponse

    @GET("stories")
    fun getStoriesWithLoc(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadStoryWithLoc(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<StoryUploadResponse>
}