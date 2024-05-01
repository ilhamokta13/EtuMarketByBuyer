package com.ilham.etumarketbybuyer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.database.BuyerDatabase
import com.ilham.etumarketbybuyer.database.FavoriteBuyer
import com.ilham.etumarketbybuyer.databinding.ItemFavproductBinding
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class FavoriteBuyerAdapter(private var context : Context, private var listproduct: List<FavoriteBuyer> )  : RecyclerView.Adapter<FavoriteBuyerAdapter.ViewHolder>()  {
    private var filmFavDB: BuyerDatabase? = null

    class ViewHolder(var binding : ItemFavproductBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemFavproductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvProductName.text = listproduct[position].nameProduct
        holder.binding.tvProductCategory.text = listproduct[position].category
        holder.binding.tvProductPrice.text = listproduct[position].price.toString()
        Glide.with(holder.itemView).load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listproduct[position].image}").into(holder.binding.ivProductImg)

//        holder.binding.btnDetail.setOnClickListener{
//            val listproducts = listproduct[position]
//            val category = listproducts.category
//            val description = listproducts.description
//            val id = listproducts.id
//            val image = listproducts.image
//            val latitude = listproducts.latitude
//            val longitude = listproducts.longitude
//            val nameproduct = listproducts.nameProduct
//            val price = listproducts.price
//            val releaseDate = listproducts.releaseDate
//            val bundle = Bundle()
//            val detail = DataAllProduct(category,description,id,image,latitude,longitude,nameproduct, price,releaseDate)
//            bundle.putParcelable("detail",detail)
//            Navigation.findNavController(it).navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
//        }

        holder.binding.favoritesCheckBox.setOnClickListener {
            var isFavorites = holder.binding.favoritesCheckBox.isChecked
            if (isFavorites){
                filmFavDB = BuyerDatabase.getInstance(it.context)

                AlertDialog.Builder(it.context)
                    .setTitle("Hapus Data")
                    .setMessage("Yakin Hapus Data")
                    .setPositiveButton("Ya") { _: DialogInterface, _: Int ->
                        GlobalScope.async {
                            val result = filmFavDB?.buyerDao()!!.deleteBuyerFavorites(
                                listproduct[position])

                            (holder.itemView.context as MainActivity).runOnUiThread {
                                if (result != 0) {
                                    Toast.makeText(it.context, "Data ${listproduct[position].nameProduct} Terhapus", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(it.context, "Data ${listproduct[position].nameProduct} Gagal terhapus", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("Tidak") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        isFavorites = holder.binding.favoritesCheckBox.isChecked
                    }
                    .show()


            }
        }

    }

    override fun getItemCount(): Int {
       return listproduct.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataUser(itemMovie: ArrayList<FavoriteBuyer>) {
        this.listproduct = itemMovie
    }
}