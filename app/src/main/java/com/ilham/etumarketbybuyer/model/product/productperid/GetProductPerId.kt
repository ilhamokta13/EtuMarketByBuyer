package com.ilham.etumarketbybuyer.model.product.productperid

import com.google.gson.annotations.SerializedName

data class GetProductPerId(
    @SerializedName("data")
    val `data`: DataPerId,
    @SerializedName("message")
    val message: String
)
