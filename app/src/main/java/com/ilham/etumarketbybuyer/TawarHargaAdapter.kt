package com.ilham.etumarketbybuyer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilham.etumarketbybuyer.databinding.ItemHistoryBinding
import com.ilham.etumarketbybuyer.databinding.ItemTawarhargaBinding
import com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga.Data

class TawarHargaAdapter(var listtawaran : List<Data>) : RecyclerView.Adapter<TawarHargaAdapter.ViewHolder>() {
    class ViewHolder (var binding : ItemTawarhargaBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemTawarhargaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listtawaran[position]

        val namaproduk = item.product.nameProduct
        holder.binding.NameProduct.text = "Nama produk :  $namaproduk"
        val hargaproduk = item.offer.price
        holder.binding.HargaTawar.text = "Harga Tawar : $hargaproduk"
        val status = item.offer.status
        holder.binding.StatusTawar.text = "Status Tawaran : $status"


    }

    override fun getItemCount(): Int {
       return listtawaran.size
    }
}