package com.ilham.etumarketbybuyer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemProductBinding
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel

class BuyerAdapter( var listproduct: List<DataAllProduct>) : RecyclerView.Adapter<BuyerAdapter.ViewHolder>() {


    class ViewHolder(var binding : ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvProductName.text = listproduct[position].nameProduct
        holder.binding.tvProductCategory.text = listproduct[position].category
        holder.binding.tvProductPrice.text = listproduct[position].price.toString()
        Glide.with(holder.itemView).load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listproduct[position].image}").into(holder.binding.ivProductImg)

        holder.binding.btnDetail.setOnClickListener{
            var detail = Bundle()
            var id = listproduct[position].id

            detail.putString("detail", id)
            Navigation.findNavController(it).navigate(R.id.action_homeFragment2_to_detailFragment, detail)
        }





    }

    override fun getItemCount(): Int {
        return listproduct.size
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun setDataUser(list : List<DataAllProduct>){
//        listproduct = list
//        notifyDataSetChanged()
//    }
}