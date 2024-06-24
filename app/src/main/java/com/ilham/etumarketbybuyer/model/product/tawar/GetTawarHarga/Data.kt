package com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("offer")
    val offer: Offer,
    @SerializedName("product")
    val product: Product
)