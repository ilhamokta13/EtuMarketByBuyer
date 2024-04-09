package com.ilham.etumarketbybuyer.model.transaksi.riwayat


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    @SerializedName("ProductID")
    val productID: ProductID,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("status")
    val status: String
)