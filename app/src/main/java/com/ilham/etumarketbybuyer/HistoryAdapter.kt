package com.ilham.etumarketbybuyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemHistoryBinding
import com.ilham.etumarketbybuyer.model.alltransaksi.Data
import com.ilham.etumarketbybuyer.model.transaksi.riwayat.DataHistory

class HistoryAdapter(var listhistory : List<DataHistory>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(var binding : ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val dataItem = listhistory[position]
            // Ambil produk pertama sebagai contoh
            val product = dataItem.products.firstOrNull() ?: return // Jika tidak ada produk, hentikan proses lebih lanjut

            val namaproduk = product.productID.nameProduct
            holder.binding.RiwayatName.text = "Nama Produk :  $namaproduk"
            val price = product.productID.price.toString()
            holder.binding.RiwayatPrice.text = "Harga Produk : $price"

            val jumlah = product.quantity
            holder.binding.RiwayatJumlah.text = "Jumlah : $jumlah"

            val status = product.status
            holder.binding.RiwayatStatus.text = "Status Barang: $status"

            val statuspembayaran = listhistory[position].status
            holder.binding.RiwayatStatusPembayaran.text = "Status Pembayaran : $statuspembayaran"


            val totalharga = product.total
            holder.binding.RiwayatTotal.text = "Total Harga : $totalharga"

            Glide.with(holder.itemView.context)
                .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${product.productID.image}")
                .into(holder.binding.cartImage)

        holder.binding.historyButton.setOnClickListener {
            val kodetransaksi = dataItem.kodeTransaksi
            val productId = product.productID.id
            val bundle = Bundle()
            bundle.putString("transcode", kodetransaksi)
            bundle.putString("productsid", productId)
            Navigation.findNavController(it).navigate(R.id.action_historyFragment2_to_detailHistoryFragment, bundle)


        }

    }

    override fun getItemCount(): Int {
       return listhistory.size
    }
}