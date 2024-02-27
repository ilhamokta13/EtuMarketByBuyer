package com.ilham.etumarketbybuyer.model.changepass


import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)