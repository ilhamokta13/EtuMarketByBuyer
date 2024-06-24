package com.ilham.etumarketbybuyer.pengiriman


import com.google.gson.annotations.SerializedName

data class PengirimanResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)