package com.ilham.etumarketbybuyer.model.product.tawar


import com.google.gson.annotations.SerializedName

data class Offer(
    @SerializedName("buyerID")
    val buyerID: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("status")
    val status: String
)