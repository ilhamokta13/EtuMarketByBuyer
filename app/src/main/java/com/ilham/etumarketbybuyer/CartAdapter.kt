package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemCartBinding
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.cart.usercart.ProductID
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel

    class CartAdapter(private var listCart : List<Product>, var context : Context, var cartVm : CartViewModel, var token : String, var lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemCartBinding) :
            RecyclerView.ViewHolder(binding.root) {

            init {

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
        var jumlah = listCart[position].quantity

        val hargatotal1 = price * jumlah

        holder.binding.tvJumlahCart.text = jumlah.toString()


        holder.binding.cartTotal.text = "Harga Total : ${hargatotal1.toString()}"



//        holder.binding.btnEditJumlahCart.setOnClickListener {
//            var edit = Bundle()
//            edit.putSerializable("edit", listCart[position])
//            Navigation.findNavController(it)
//                .navigate(R.id.action_cartFragment_to_editCartFragment, edit)
//        }



        holder.binding.addJumlahCart.setOnClickListener {
            if (jumlah == 1000000){
                Toast.makeText(context, "Produk mencapai batas maksimal", Toast.LENGTH_SHORT).show()
            } else {
                jumlah++
                holder.binding.tvJumlahCart.text = jumlah.toString()
                val dataAddCart = DataAddCart(listOf(listCart[position].productID.id), listOf(jumlah))

                listCart[position].quantity = jumlah

                cartVm.dataUpdateCart.observe(lifecycleOwner){

                    if (it.message == "Update Cart") {
                        listCart[position].quantity = jumlah
                        val price = listCart[position].productID.price
                        var jumlah = listCart[position].quantity

                        val hargatotal1 = price * jumlah

                        holder.binding.tvJumlahCart.text = jumlah.toString()


                        holder.binding.cartTotal.text = "Harga Total : ${hargatotal1.toString()}"
                    }

                }
                cartVm.updatecart(token,dataAddCart)


            }
        }

        holder.binding.decJumlahCart.setOnClickListener {
            if (jumlah == 0){
                Toast.makeText(context, "Produk mencapai batas maksimal", Toast.LENGTH_SHORT).show()
            } else {
                jumlah--
                holder.binding.tvJumlahCart.text = jumlah.toString()
                val dataAddCart = DataAddCart(listOf(listCart[position].productID.id), listOf(jumlah))
                listCart[position].quantity = jumlah

                cartVm.dataUpdateCart.observe(lifecycleOwner){

                    if (it.message == "Update Cart") {
                        listCart[position].quantity = jumlah
                        val price = listCart[position].productID.price
                        var jumlah = listCart[position].quantity

                        val hargatotal1 = price * jumlah

                        holder.binding.tvJumlahCart.text = jumlah.toString()


                        holder.binding.cartTotal.text = "Harga Total : ${hargatotal1.toString()}"
                    }

                }
                cartVm.updatecart(token,dataAddCart)



            }
        }






        Glide.with(holder.itemView)
            .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listCart[position].productID.image}")
            .into(holder.binding.cartImage)





    }

        override fun getItemCount(): Int {
            return listCart.size
        }








}