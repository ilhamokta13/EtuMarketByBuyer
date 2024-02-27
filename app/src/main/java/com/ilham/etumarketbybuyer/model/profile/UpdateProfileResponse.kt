package com.ilham.etumarketbybuyer.model.profile


import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("data")
    val `data`: DataProfile,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)