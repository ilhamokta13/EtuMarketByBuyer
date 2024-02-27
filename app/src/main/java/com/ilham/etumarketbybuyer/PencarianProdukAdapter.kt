package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ilham.etumarketbybuyer.databinding.ItemSearchBinding
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct

class PencarianProdukAdapter(private val context: Context, private val listproduct : List<DataAllProduct>) : RecyclerView.Adapter<PencarianProdukAdapter.ViewHolder>()  {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE)
    inner class ViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSearch(item: DataAllProduct, sharedPreferences: SharedPreferences) {
            binding.tvDestination.setOnClickListener {
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("keyProduct", item.nameProduct)
                editor.apply()

                val navController = Navigation.findNavController(binding.root)
                navController.previousBackStackEntry?.savedStateHandle?.set("selected_destination", item.nameProduct)
                navController.navigate(R.id.action_pilihProdukFragment_to_homeFragment2)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listproduct[position]
        holder.bindSearch(item, sharedPreferences)
        holder.binding.tvDestination.text = item.nameProduct

        holder.itemView.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            // Menyimpan data ke SharedPreferences
            editor.putString("keyFrom", item.nameProduct)
            editor.apply()

            Navigation.findNavController(it).navigate(R.id.action_pilihProdukFragment_to_homeFragment2)
        }
    }

    override fun getItemCount(): Int {
        return listproduct.size
    }
}