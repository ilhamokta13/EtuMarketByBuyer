package com.ilham.etumarketbybuyer.pengiriman


import com.google.gson.annotations.SerializedName

data class PostPengiriman(
    @SerializedName("destinationLatitude")
    val destinationLatitude: Double,
    @SerializedName("destinationLongitude")
    val destinationLongitude: Double,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String
)