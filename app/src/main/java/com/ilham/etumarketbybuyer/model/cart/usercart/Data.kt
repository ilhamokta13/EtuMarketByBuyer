package com.ilham.etumarketbybuyer.model.cart.usercart


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    @SerializedName("_id")
    val id: String,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("__v")
    val v: Int
):Serializable