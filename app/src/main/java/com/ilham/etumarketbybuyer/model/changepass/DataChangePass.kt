package com.ilham.etumarketbybuyer.model.changepass


import com.google.gson.annotations.SerializedName

data class DataChangePass(
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("oldPassword")
    val oldPassword: String
)