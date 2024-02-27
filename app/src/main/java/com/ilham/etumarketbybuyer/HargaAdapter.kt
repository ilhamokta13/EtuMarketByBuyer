package com.ilham.etumarketbybuyer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilham.etumarketbybuyer.databinding.ItemHargaBinding
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel

class HargaAdapter(private val listHarga : List<Product>, private val cartVm : CartViewModel) : RecyclerView.Adapter<HargaAdapter.ViewHolder>() {
    class ViewHolder(var binding : ItemHargaBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = ItemHargaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtAdult. text = listHarga[position].quantity.toString()
        holder.binding.priceadult.text = listHarga[position].productID.price.toString()

        val quantity = listHarga[position].quantity
        val price = listHarga[position].productID.price

        val total1 = quantity * price


        holder.binding.tvPriceProduct.text = total1.toString()
//        cartVm.saveHargaCart(total1)


    }

    override fun getItemCount(): Int {
       return listHarga.size
    }
}