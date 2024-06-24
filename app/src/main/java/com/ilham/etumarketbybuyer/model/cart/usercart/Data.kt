package com.ilham.etumarketbybuyer.model.cart.usercart


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("destination")
    val destination: Destination,
    @SerializedName("_id")
    val id: String,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("shippingCost")
    val shippingCost: Int,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("__v")
    val v: Int
)