package com.ilham.etumarketbybuyer.database

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ilham.etumarketbybuyer.model.product.allproduct.SellerID
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Parcelize
@Entity
data class FavoriteBuyer(
    @PrimaryKey
    @NonNull
    val id: String,
    @ColumnInfo(name="category")
    val category: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "nameProduct")
    val nameProduct: String,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "releaseDate")
    val releaseDate: String,



):Parcelable
