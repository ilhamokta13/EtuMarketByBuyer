package com.ilham.etumarketbybuyer.model.profile


import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    @SerializedName("data")
    val `data`: UserProfile,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)