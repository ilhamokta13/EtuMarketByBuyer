package com.ilham.etumarketbybuyer.model.cart.usercart


import com.google.gson.annotations.SerializedName

data class GetCartResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)