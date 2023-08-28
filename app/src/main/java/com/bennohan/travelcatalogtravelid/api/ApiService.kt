package com.bennohan.travelcatalogtravelid.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //AUTH
    //login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("phone_or_email") phoneOrEmail: String?,
        @Field("password") password: String?,
    ): String

    //Register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("phone_or_email") phoneOrEmail: String?,
        @Field("password") password: String?,
        @Field("confirm_password") confirmPassword: String?,
    ): String

    //Logout
    @POST("logout")
    suspend fun logout(
    ): String

    //Edit Profile
    @FormUrlEncoded
    @POST("user/edit-profile")
    suspend fun editProfile(
        @Field("name") name: String?,
    ): String

    @Multipart
    @POST("user/edit-profile")
    suspend fun editProfilePhoto(
        @Field("name") name: String?,
        @Part photoProfile : MultipartBody.Part?
    ): String


    //DESTINATION
    //GET DESTINATION LIST
    @GET("destination/list")
    suspend fun destinationList(): String

    @GET("list-category")
    suspend fun destinationCategory(): String

    //GET DESTINATION BY ID
    @GET("destination/{id}/detail")
    suspend fun destinationById(
        @Path("id") id : Int
    ): String

    //GET DESTINATION BY Category
    @GET("destination/filter-by-category/{id}")
    suspend fun getDestinationByCategory(
        @Path("id") idCategory : Int
    ): String


    //USER
    //SAVE_DESTINATION
    @FormUrlEncoded
    @POST("user/save-destination")
    suspend fun saveDestination(
        @Field("destination_id") id : Int
    ): String



}