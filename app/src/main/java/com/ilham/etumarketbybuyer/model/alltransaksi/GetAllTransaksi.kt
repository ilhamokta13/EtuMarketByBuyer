package com.ilham.etumarketbybuyer.model.alltransaksi


import com.google.gson.annotations.SerializedName

data class GetAllTransaksi(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)