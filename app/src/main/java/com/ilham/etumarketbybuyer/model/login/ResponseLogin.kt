package com.ilham.etumarketbybuyer.model.login

import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String
)
