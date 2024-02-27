package com.ilham.etumarketbybuyer.model.cart.usercart


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetCartResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
):Serializable