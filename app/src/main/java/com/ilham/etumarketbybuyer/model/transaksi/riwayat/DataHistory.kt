package com.ilham.etumarketbybuyer.model.transaksi.riwayat


import com.google.gson.annotations.SerializedName

data class DataHistory(
    @SerializedName("_id")
    val id: String,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("Products")
    val products: List<Product>,
    @SerializedName("status")
    val status: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("__v")
    val v: Int
)