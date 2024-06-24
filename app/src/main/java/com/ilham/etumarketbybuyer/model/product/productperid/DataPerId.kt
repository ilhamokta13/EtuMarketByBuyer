package com.ilham.etumarketbybuyer.model.product.productperid

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataPerId(
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("nameProduct")
    val nameProduct: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("sellerID")
    val sellerID: SellerID,
    @SerializedName("__v")
    val v: Int
):Serializable
