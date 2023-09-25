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

    //Logout
    @POST("refresh-token")
    suspend fun refreshToken(
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
        @Part("name") name: String?,
        @Part photoProfile: MultipartBody.Part?
    ): String

    @Multipart
    @GET("destination/filter-by-category/{id}")
    suspend fun filterCategory(
        @Path("id") id: Int
    ): String


    //DESTINATION
    //GET DESTINATION LIST
    @GET("destination/list")
    suspend fun destinationList(): String

    @GET("list-category")
    suspend fun destinationCategory(): String

    @GET("list-province")
    suspend fun destinationProvince(): String

    //GET DESTINATION BY ID
    @GET("destination/{id}/detail")
    suspend fun destinationById(
        @Path("id") id: Int
    ): String

    //GET DESTINATION BY Category
    @GET("destination/filter-by-category/{id}")
    suspend fun getDestinationByCategory(
        @Path("id") idCategory: Int
    ): String

    @GET("destination/filter-by-province/{id}")
    suspend fun getDestinationByProvince(
        @Path("id") idCategory: Int
    ): String


    //USER
    //SAVE_DESTINATION
    @FormUrlEncoded
    @POST("user/save-destination")
    suspend fun saveDestination(
        @Field("destination_id") id: Int
    ): String


    //REVIEW
    //ADD_REVIEW
    @FormUrlEncoded
    @POST("review/add")
    suspend fun addReview(
        @Field("rating") rating: Int,
        @Field("review_description") review_description : String,
        @Field("destination_id") id : Int
    ): String

    //GET USER REVIEW LIST
    @GET("user/user-review")
    suspend fun getDestinationReviewList(
    ): String


}