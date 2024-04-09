package com.ilham.etumarketbybuyer.model.transaksi.riwayat


import com.google.gson.annotations.SerializedName

data class GetRiwayatResponse(
    @SerializedName("data")
    val `data`: List<DataHistory>,
    @SerializedName("message")
    val message: String
)