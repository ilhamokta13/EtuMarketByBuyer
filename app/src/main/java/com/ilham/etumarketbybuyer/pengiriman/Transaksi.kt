package com.ilham.etumarketbybuyer.pengiriman


import com.google.gson.annotations.SerializedName

data class Transaksi(
    @SerializedName("destination")
    val destination: Destination,
    @SerializedName("_id")
    val id: String,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("Products")
    val products: List<Product>,
    @SerializedName("shippingCost")
    val shippingCost: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("user")
    val user: String,
    @SerializedName("__v")
    val v: Int
)