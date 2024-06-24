package com.ilham.etumarketbybuyer.model.changepass


import com.google.gson.annotations.SerializedName

data class PostForgotPass(
    @SerializedName("email")
    val email: String
)