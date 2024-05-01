package com.ilham.etumarketbybuyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemHistoryBinding
import com.ilham.etumarketbybuyer.model.transaksi.riwayat.DataHistory

class HistoryAdapter(var listhistory : List<DataHistory>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var filteredList: List<DataHistory> = ArrayList()
    var redItems: MutableList<DataHistory> = mutableListOf()
    var blueItems: MutableList<DataHistory> = mutableListOf()


    class ViewHolder(var binding : ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val dataItem = listhistory[position]

        val item = if (position < blueItems.size) blueItems[position] else redItems[position - blueItems.size]

        val product =  item.products.firstOrNull() ?: return

            val namaproduk = product.productID.nameProduct
            holder.binding.RiwayatName.text = "Nama Produk :  $namaproduk"
            val price = product.productID.price
            holder.binding.RiwayatPrice.text = "Harga Produk : $price"

            val jumlah = product.quantity
            holder.binding.RiwayatJumlah.text = "Jumlah : $jumlah"

            val status = product.status
            holder.binding.StatusPengiriman.text = "Status Pengiriman: $status"

            val totalPrice = jumlah * price

            val statuspembayaran = item.status
            holder.binding.StatusPembayaran.text = "Status Pembayaran: $statuspembayaran"


//            val totalharga = dataItem.total
            holder.binding.RiwayatTotal.text = "Total Harga : $totalPrice"

            Glide.with(holder.itemView.context)
                .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${product.productID.image}")
                .into(holder.binding.cartImage)

            holder.binding.historyButton.setOnClickListener {
                val kodetransaksi = item.kodeTransaksi
                val productId = product.productID.id
                val bundle = Bundle()
                bundle.putString("transcode", kodetransaksi)
                bundle.putString("productsid", productId)
                Navigation.findNavController(it).navigate(R.id.action_historyFragment2_to_detailHistoryFragment, bundle)
            }


            if (product.status == "Selesai") {
                holder.binding.itemlist.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            } else {
                // If not delivered, set the default background color
                holder.binding.itemlist.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
            }











    }



    override fun getItemCount(): Int {
        return blueItems.size + redItems.size
    }

    fun filter(text: String) {
        listhistory = if (text.isEmpty()) {
            filteredList
        } else {
            val tempList = ArrayList<DataHistory>()
            for (item in filteredList) {
                if (item.products.firstOrNull()?.productID!!.nameProduct!!
                        .contains(text, ignoreCase = true)
                ) {
                    tempList.add(item)
                }
            }
            tempList
        }
        separateItemsByColor()
        notifyDataSetChanged()
    }

    // Memisahkan item berdasarkan warna
    fun separateItemsByColor() {
        redItems.clear()
        blueItems.clear()
        for (item in listhistory) {
            if (item.products.firstOrNull()?.status == "Selesai") {
                redItems.add(item)
            } else {
                blueItems.add(item)
            }
        }
    }




}