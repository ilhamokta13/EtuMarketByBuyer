package com.ilham.etumarketbybuyer.model.status


import com.google.gson.annotations.SerializedName

data class PostUpdateStatus(
    @SerializedName("image")
    val image: String,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("productID")
    val productID: String,
    @SerializedName("status")
    val status: String
)