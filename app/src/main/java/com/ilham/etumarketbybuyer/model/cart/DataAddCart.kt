package com.ilham.etumarketbybuyer.model.cart


import com.google.gson.annotations.SerializedName

data class DataAddCart(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("productID")
    val productID: List<String>,
    @SerializedName("quantity")
    val quantity: List<Int>
)