package com.ilham.etumarketbybuyer.model.profile


import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("email")
    var email: String,
    @SerializedName("fullName")
    var fullName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("shopName")
    val shopName: String,
    @SerializedName("telp")
    var telp: String,
    @SerializedName("__v")
    val v: Int
)