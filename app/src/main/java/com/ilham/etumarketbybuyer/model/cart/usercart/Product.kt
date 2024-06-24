package com.ilham.etumarketbybuyer.model.cart.usercart


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    @SerializedName("productID")
    val productID: ProductID,
    @SerializedName("quantity")
    var quantity: Int
)