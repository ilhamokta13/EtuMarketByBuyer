package com.ilham.etumarketbybuyer.model.changepass


import com.google.gson.annotations.SerializedName

data class PostNewPassword(
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("token")
    val token: String
)