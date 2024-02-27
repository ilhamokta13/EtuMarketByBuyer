package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilham.etumarketbybuyer.databinding.FragmentCartBinding
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    lateinit var binding : FragmentCartBinding
    lateinit var pref : SharedPreferences
    private lateinit var idUser : String
    lateinit var cartVm : CartViewModel
    lateinit var cartAdapter: CartAdapter
    lateinit var productVm : ProductViewModel
    private lateinit var idProduct: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        cartVm = ViewModelProvider(this).get(CartViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        idUser = pref.getString("token","").toString()

        val jmlcart = cartVm.getCart()

//        val belanja = setCart(jmlcart)
//
//        binding.tvJumlahCart.text = belanja.toString()




        if (pref.getString("token", "")!!.isEmpty()) {
            binding.rvListCart.visibility = View.GONE
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Login")
                .setMessage("Anda Belum Login")
                .setCancelable(false)
                .setNegativeButton("Cancel") { dialog, which ->
                    // Respond to negative button press
                    findNavController().navigate(R.id.action_cartFragment_to_homeFragment2)
                }
                .setPositiveButton("Login") { dialog, which ->
                    // Respond to positive button press
                    findNavController().navigate(R.id.action_cartFragment_to_loginFragment)
                }
                .show()
        } else if (pref.getString("token", "")!!.isNotEmpty()) {
            getDataCart()
//            getharga()
        }

       binding.btnDeletecart.setOnClickListener {
           cartVm.deletecart(idUser)
            Toast.makeText(requireContext(), "Item telah dihapus", Toast.LENGTH_SHORT).show()
        }



    }

    fun getDataCart(){
        val token = pref.getString("token","").toString()
        cartVm.CartUser(token)
        cartVm.dataCartUser.observe(viewLifecycleOwner, Observer{
            binding.rvListCart.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL,false)
            binding.rvListCart.adapter = CartAdapter(it, cartVm) {
               val harga =  it.productID.price
                val quantity = it.quantity
                val jumlah = harga * quantity
                val jmlcart = cartVm.getCart()

            }

//            val getharga1 = cartVm.getHargaCart()
            val getharga2 = cartVm.calculateTotalPrice(it)
//            val total = getharga1 + getharga2
            binding.tvPriceProduct.text = getharga2.toString()

//            val getharga1 = cartVm.getHargaCart()
//            val getharga2 = cartVm.getHargaCart()
//            val total = getharga1 + getharga2
//            binding.tvPriceProduct.text = total.toString()

                



        })
    }

//    fun getharga(){
//        cartVm.dataCartUser.observe(viewLifecycleOwner, Observer{
//            binding.rvListHarga.layoutManager = LinearLayoutManager(context,
//                LinearLayoutManager.VERTICAL,false)
//            binding.rvListHarga.adapter = HargaAdapter(it, cartVm)
//
//
//
//
//        })
//    }
















}