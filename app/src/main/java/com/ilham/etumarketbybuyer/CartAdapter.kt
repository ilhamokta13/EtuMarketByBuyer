package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemCartBinding
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.cart.usercart.ProductID
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel

    class CartAdapter(private val listCart : List<Product>, private val cartVm : CartViewModel, private val onSelect:(Product) -> Unit) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

        class ViewHolder(var binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.cartName.text = listCart[position].productID.nameProduct
        holder.binding.cartPrice.text = listCart[position].productID.price.toString()
        holder.binding.cartJumlah.text = listCart[position].quantity.toString()

        val price = listCart[position].productID.price
        val jumlah = listCart[position].quantity

        val hargatotal1 = price * jumlah


        holder.binding.cartTotal.text = hargatotal1.toString()
//        cartVm.saveHargaCart(hargatotal1)





        holder.binding.btnEditJumlahCart.setOnClickListener{
            var edit = Bundle()
            edit.putSerializable("edit", listCart[position])
            Navigation.findNavController(it).navigate(R.id.action_cartFragment_to_editCartFragment,edit)
        }




        Glide.with(holder.itemView).load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listCart[position].productID.image}").into(holder.binding.cartImage)

        holder.binding.CartDetail.setOnClickListener {
            onSelect(listCart[position])

        }


//        holder.binding.cvCart.setOnClickListener {
//            onSelect(listCart[position])
//        }





//        holder.binding.cvCart.setOnClickListener {
//            onSelect(listCart[position])
//        }


//        val id = listCart[position].productID.id
//        cartVm.saveIdCart(id)
    }

    override fun getItemCount(): Int {
        return listCart.size
    }
}