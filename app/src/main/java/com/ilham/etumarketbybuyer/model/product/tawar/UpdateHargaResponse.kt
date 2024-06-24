package com.ilham.etumarketbybuyer.model.product.tawar


import com.google.gson.annotations.SerializedName

data class UpdateHargaResponse(
    @SerializedName("data")
    val `data`: DataHarga,
    @SerializedName("message")
    val message: String
)