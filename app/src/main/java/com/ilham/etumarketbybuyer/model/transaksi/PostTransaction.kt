package com.ilham.etumarketbybuyer.model.transaksi


import com.google.gson.annotations.SerializedName

data class PostTransaction(
    @SerializedName("destination")
    val destination: Destination,
    @SerializedName("id_barang")
    val idBarang: List<String>,
    @SerializedName("quantity")
    val quantity: List<Int>,
    @SerializedName("shippingCost")
    val shippingCost: Int
)