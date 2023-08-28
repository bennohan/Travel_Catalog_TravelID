package com.bennohan.travelcatalogtravelid.database


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Destination(
    @Expose
    @SerializedName("address")
    val address: String?,
    @Expose
    @SerializedName("category")
    val category: String?,
    @Expose
    @SerializedName("city")
    val city: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("description")
    val description: String?,
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("latitude")
    val latitude: String?,
    @Expose
    @SerializedName("longitude")
    val longitude: String?,
    @Expose
    @SerializedName("name")
    val name: String?,
//    @Expose
//    @SerializedName("photo")
//    val photo: List<String>?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
//    @Expose
//    @SerializedName("photo")
//    val photoS: String?,
    @Expose
    @SerializedName("price")
    val price: String?,
    @Expose
    @SerializedName("province")
    val province: String?,
    @Expose
    @SerializedName("rating")
    val rating: String?,
//    val rating: Float?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
)