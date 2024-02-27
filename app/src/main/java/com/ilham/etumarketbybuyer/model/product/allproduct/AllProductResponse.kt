package com.ilham.etumarketbybuyer.model.product.allproduct

import com.google.gson.annotations.SerializedName

data class AllProductResponse(
    @SerializedName("data")
    val `data`: List<DataAllProduct>,
    @SerializedName("message")
    val message: String

)
