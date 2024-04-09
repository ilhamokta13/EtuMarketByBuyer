package com.ilham.etumarketbybuyer

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemCartBinding
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.cart.usercart.ProductID
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel

    class CartAdapter(private var listCart : List<Product>, var cartVm : CartViewModel, var token : String) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemCartBinding) :
            RecyclerView.ViewHolder(binding.root) {

            init {
//                binding.decJumlahCart.setOnClickListener {
//                    val position = adapterPosition
//                    if (position != RecyclerView.NO_POSITION) {
//                        val product = listCart[position]
//                        val dataAddCart = DataAddCart(listOf(product.productID.id),
//                            listOf(product.quantity - 1)
//                        )
//                        cartVm.updatecart(token, dataAddCart)
//
//
//                    }
//
//
//                }

                binding.decJumlahCart.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val product = listCart[position]
                        val updatedQuantity = product.quantity - 1
                        if (updatedQuantity >= 0) {
                            val dataAddCart = DataAddCart(listOf(product.productID.id), listOf(updatedQuantity))
                            cartVm.updatecart(token, dataAddCart)
                            binding.tvJumlahCart.text = updatedQuantity.toString()
                        }
                    }
                }



//                binding.addJumlahCart.setOnClickListener {
//                    val position = adapterPosition
//                    if (position != RecyclerView.NO_POSITION) {
//                        val product = listCart[position]
//                        val dataAddCart = DataAddCart(listOf(product.productID.id),
//                            listOf(product.quantity + 1)
//                        )
//                        cartVm.updatecart(token, dataAddCart)
//                    }
//                }

                binding.addJumlahCart.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val product = listCart[position]
                        val updatedQuantity = product.quantity + 1
                        val dataAddCart = DataAddCart(listOf(product.productID.id), listOf(updatedQuantity))
                        cartVm.updatecart(token, dataAddCart)
                        binding.tvJumlahCart.text = updatedQuantity.toString()
                    }
                }
            }
        }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val namaproduk = listCart[position].productID.nameProduct
        holder.binding.cartName.text = "nama produk : $namaproduk"
        val hargaproduk = listCart[position].productID.price
        holder.binding.cartPrice.text = "harga produk : $hargaproduk"
        val quantitas = listCart[position].quantity
        holder.binding.cartJumlah.text = "Jumlah Barang : $quantitas"


        val id = listCart[position].id
//        cartVm.saveIdCart(id)

        val price = listCart[position].productID.price
        val jumlah = listCart[position].quantity

        val hargatotal1 = price * jumlah


        holder.binding.cartTotal.text = "Harga Total : ${hargatotal1.toString()}"



//        holder.binding.btnEditJumlahCart.setOnClickListener {
//            var edit = Bundle()
//            edit.putSerializable("edit", listCart[position])
//            Navigation.findNavController(it)
//                .navigate(R.id.action_cartFragment_to_editCartFragment, edit)
//        }






        Glide.with(holder.itemView)
            .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listCart[position].productID.image}")
            .into(holder.binding.cartImage)





    }

        override fun getItemCount(): Int {
            return listCart.size
        }

        fun getAllProducts(): List<Product> {
            return listCart
        }






}