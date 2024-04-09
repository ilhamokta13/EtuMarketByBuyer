package com.ilham.etumarketbybuyer.model.product.productshopname


import com.google.gson.annotations.SerializedName

data class GetProductspershop(
    @SerializedName("data")
    val `data`: List<DataPerShop>,
    @SerializedName("message")
    val message: String
)