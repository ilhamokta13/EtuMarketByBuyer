package com.ilham.etumarketbybuyer.model.product.tawar


import com.google.gson.annotations.SerializedName

data class UpdateProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)