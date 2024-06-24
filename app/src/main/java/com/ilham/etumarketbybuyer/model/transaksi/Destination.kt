package com.ilham.etumarketbybuyer.model.transaksi


import com.google.gson.annotations.SerializedName

data class Destination(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)