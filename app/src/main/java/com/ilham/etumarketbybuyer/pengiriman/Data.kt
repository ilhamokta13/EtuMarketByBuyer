package com.ilham.etumarketbybuyer.pengiriman


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("distanceInKm")
    val distanceInKm: Double,
    @SerializedName("shippingCost")
    val shippingCost: Int,
    @SerializedName("transaksi")
    val transaksi: Transaksi
)