package com.ilham.etumarketbybuyer.model.transaksi


import com.google.gson.annotations.SerializedName

data class MidtransResponse(
    @SerializedName("redirect_url")
    val redirectUrl: String,
    @SerializedName("token")
    val token: String
)