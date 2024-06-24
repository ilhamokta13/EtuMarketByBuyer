package com.ilham.etumarketbybuyer.pengiriman


import com.google.gson.annotations.SerializedName

data class Destination(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)