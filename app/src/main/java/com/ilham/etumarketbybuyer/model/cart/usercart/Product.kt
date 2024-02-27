package com.ilham.etumarketbybuyer.model.cart.usercart


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("_id")
    val id: String,
    @SerializedName("productID")
    val productID: ProductID,
    @SerializedName("quantity")
    val quantity: Int
):Serializable