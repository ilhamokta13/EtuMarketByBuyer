package com.ilham.etumarketbybuyer.model.cart


import com.google.gson.annotations.SerializedName

data class ResponseAddCart(
    @SerializedName("message")
    val message: String
)