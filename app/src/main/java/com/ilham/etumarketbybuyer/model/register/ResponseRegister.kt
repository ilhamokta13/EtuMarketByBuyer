package com.ilham.etumarketbybuyer.model.register

import com.google.gson.annotations.SerializedName

data class ResponseRegister(
    @field:SerializedName("data")
    val `data`: DataRegister? = null,
    @field:SerializedName("message")
    val message: String? = null
)

data class DataRegister(
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("fullName")
    val fullName: String? = null,
    @field:SerializedName("_id")
    val _id: String? = null,
    @field:SerializedName("password")
    val password: String? = null,
    @field:SerializedName("role")
    val role: String? = null,
    @field:SerializedName("telp")
    val telp: String? = null,
    @field:SerializedName("shopName")
    val shopName: String? = null,
    @field:SerializedName("__v")
    val v: Int? = null
)
