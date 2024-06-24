package com.ilham.etumarketbybuyer

import android.media.RouteListingPreference
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.ItemProductBinding
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.model.product.productshopname.DataPerShop

class ShopAdapter(private val listshop : List<DataAllProduct>) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    class ViewHolder (var binding : ItemProductBinding):RecyclerView.ViewHolder(binding.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvProductName.text = listshop[position].nameProduct
        holder.binding.tvProductCategory.text = listshop[position].category
        holder.binding.tvProductPrice.text = listshop[position].price.toString()
        holder.binding.tvStoreName.text = listshop[position].sellerID.shopName
        Glide.with(holder.itemView).load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listshop[position].image}").into(holder.binding.ivProductImg)

        holder.binding.btnDetail.setOnClickListener {
            var detail = Bundle()
            detail.putParcelable("detail", listshop[position])

            Navigation.findNavController(it).navigate(R.id.detailFragment, detail)
        }
    }

    override fun getItemCount(): Int {
        return listshop.size
    }
}