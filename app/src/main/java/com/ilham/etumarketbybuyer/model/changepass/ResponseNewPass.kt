package com.ilham.etumarketbybuyer.model.changepass


import com.google.gson.annotations.SerializedName

data class ResponseNewPass(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)