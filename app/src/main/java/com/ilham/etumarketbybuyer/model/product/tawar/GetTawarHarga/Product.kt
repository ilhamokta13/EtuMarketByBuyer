package com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("nameProduct")
    val nameProduct: String,
    @SerializedName("price")
    val price: Int
)