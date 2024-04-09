package com.ilham.etumarketbybuyer.model.transaksi


import com.google.gson.annotations.SerializedName

data class PostTransaction(
    @SerializedName("id_barang")
    val idBarang: List<String>,
    @SerializedName("jumlah")
    val jumlah: List<Int>,
    @SerializedName("total_harga")
    val totalHarga: Int
)