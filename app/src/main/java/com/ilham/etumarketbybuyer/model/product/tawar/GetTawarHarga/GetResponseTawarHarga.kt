package com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga


import com.google.gson.annotations.SerializedName

data class GetResponseTawarHarga(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)