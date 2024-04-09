package com.ilham.etumarketbybuyer.model.transaksi


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    @SerializedName("ProductID")
    val productID: ProductID,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("status")
    val status: String
)