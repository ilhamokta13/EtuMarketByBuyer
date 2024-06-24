package com.ilham.etumarketbybuyer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemCartBinding
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.CartProduct
import com.ilham.etumarketbybuyer.model.cart.usercart.Data
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel

class CartAdapter(private var listCart: List<CartProduct> , var context : Context, var cartVm : CartViewModel, var token : String, var lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

            init {

            }
        }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productWithLocation = listCart[position]
        val product = productWithLocation.product
        val productID = product.productID
        holder.binding.cartName.text = "Nama produk: ${productID.nameProduct}"
        holder.binding.cartPrice.text = "Harga produk: ${productID.price}"
        holder.binding.cartJumlah.text = "Jumlah Barang: ${product.quantity}"
        holder.binding.tvJumlahCart.text = product.quantity.toString()
        holder.binding.cartTotal.text = "Harga Total: ${(product.quantity * productID.price)}"

        Glide.with(holder.itemView)
            .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${productID.image}")
            .into(holder.binding.cartImage)

        val latitude = productWithLocation.latitude
        val longitude = productWithLocation.longitude

        //  Tambah jumlah produk

            holder.binding.addJumlahCart.setOnClickListener {
                var jumlah = product.quantity
                //Jika tombol tambah ditekan, jumlah produk akan ditingkatkan.
                // Jika jumlah mencapai batas maksimal (1,000,000), pesan Toast akan ditampilkan
                if (jumlah == 1000000) {
                    Toast.makeText(context, "Produk mencapai batas maksimal", Toast.LENGTH_SHORT).show()
                } else {
                    jumlah++
                    holder.binding.tvJumlahCart.text = jumlah.toString()
                    val dataAddCart = DataAddCart(latitude, longitude, listOf(product.productID.id), listOf(jumlah))

                    product.quantity = jumlah

                    cartVm.dataUpdateCart.observe(lifecycleOwner) {
                        if (it.message == "Update Cart") {
                            product.quantity = jumlah
                            val price = product.productID.price
                            val hargatotal1 = price * jumlah
                            holder.binding.tvJumlahCart.text = jumlah.toString()
                            holder.binding.cartTotal.text = "Harga Total: ${hargatotal1.toString()}"
                        }
                    }
                    cartVm.updatecart(token, dataAddCart)
                    notifyItemChanged(position)
                }
            }

            holder.binding.decJumlahCart.setOnClickListener {
                // Jika tombol kurang ditekan, jumlah produk akan dikurangi.
                // Jika jumlah mencapai batas minimal (0), pesan Toast akan ditampilkan.
                var jumlah = product.quantity
                if (jumlah == 0) {
                    Toast.makeText(context, "Produk mencapai batas maksimal", Toast.LENGTH_SHORT).show()
                } else {
                    jumlah--
                    holder.binding.tvJumlahCart.text = jumlah.toString()
                    val dataAddCart = DataAddCart(latitude, longitude, listOf(product.productID.id), listOf(jumlah))
                    product.quantity = jumlah

                    cartVm.dataUpdateCart.observe(lifecycleOwner) {
                        if (it.message == "Update Cart") {
                            product.quantity = jumlah
                            val price = product.productID.price
                            val hargatotal1 = price * jumlah
                            holder.binding.tvJumlahCart.text = jumlah.toString()
                            holder.binding.cartTotal.text = "Harga Total: ${hargatotal1.toString()}"
                        }
                    }
                    //Untuk kedua aksi, jumlah yang diperbarui akan dikirim ke ViewModel untuk memperbarui data keranjang di server,
                    // dan tampilan akan diperbarui sesuai dengan perubahan.
                    cartVm.updatecart(token, dataAddCart)
                    notifyItemChanged(position)
                }
            }



    }

        //Mengembalikan jumlah item dalam daftar keranjang.
        override fun getItemCount(): Int {
            return listCart.size
        }



    @SuppressLint("NotifyDataSetChanged")
    //Memperbarui daftar item keranjang
    // dan memberi tahu RecyclerView untuk memperbarui tampilannya.
    fun updateCartItems(newCartItems: List<CartProduct>) {
       listCart = newCartItems
        notifyDataSetChanged()
    }
}





















