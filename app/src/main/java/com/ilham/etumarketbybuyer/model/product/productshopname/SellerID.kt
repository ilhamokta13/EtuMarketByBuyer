package com.ilham.etumarketbybuyer.model.product.productshopname


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SellerID(
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("shopName")
    val shopName: String,
    @SerializedName("telp")
    val telp: String,
    @SerializedName("__v")
    val v: Int
):Parcelable