package com.bennohan.travelcatalogtravelid.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey
    @Expose
    @SerializedName("id_room")
    val idRoom: Int,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("phone_or_email")
    val phoneOrEmail: String?,
    @Expose
    @SerializedName("photo_profile")
    val photoProfile: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
)