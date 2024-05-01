package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilham.etumarketbybuyer.databinding.FragmentCartBinding
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.PaymentViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class CartFragment : Fragment() {
    lateinit var binding: FragmentCartBinding
    lateinit var pref: SharedPreferences
    private lateinit var idUser: String
    lateinit var cartVm: CartViewModel
    private lateinit var cartadapter: CartAdapter
    lateinit var productVm: ProductViewModel
    lateinit var paymentVm : PaymentViewModel
    private lateinit var idProduct: String
    private var cartItems: List<Product> = listOf()

    private lateinit var handler: Handler
    private val interval: Long = 3600000 // 1 jam


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        cartVm = ViewModelProvider(this).get(CartViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)
        idUser = pref.getString("token", "").toString()


        // Inisialisasi handler
        handler = Handler(Looper.getMainLooper())

        // Mulai pengulangan
        startRepeatingTask()




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



        }



        binding.btnDeletecart.setOnClickListener {
            cartVm.deletecart(idUser)
            Toast.makeText(requireContext(), "Item telah dihapus", Toast.LENGTH_SHORT).show()
        }



        binding.btnCheckout.setOnClickListener {

            cartVm.dataCartUser.observe(viewLifecycleOwner, Observer { cartItems ->
                binding.rvListCart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                if (cartItems.isNullOrEmpty()) {
                    // Handle the case when the cart is empty
                    // For example, you can show a message or hide the price view
                    binding.tvPriceProduct.text = "0"
                    // You might want to update other UI elements accordingly
                } else {
                    var totalPrice = 0.0
                    val listOfIds = mutableListOf<String>()
                    val listOfQuantities = mutableListOf<Int>()

                    for (product in cartItems) {
                        val idProductCart = product.productID.id
                        val productName = product.productID.nameProduct
                        val quantity = product.quantity
                        val price = product.productID.price
                        val keseluruhanharga = quantity * price.toDouble()
                        totalPrice += keseluruhanharga
                        listOfIds.add(idProductCart)
                        listOfQuantities.add(quantity)

                    }

                    binding.tvPriceProduct.text = totalPrice.toString()

                    val dataCart = PostTransaction(listOfIds, listOfQuantities, totalPrice.toInt())
                    val token = pref.getString("token", "").toString()

                    paymentVm.postpayment(token, dataCart)
                }

            })
            paymentVm.midtransResponse.observe(viewLifecycleOwner){
                if (it.message == "Transaksi berhasil dibuat") {

                    val tokenmidtrans = it.midtransResponse.token
                    val redirectUrl = it.midtransResponse.redirectUrl


                    Log.d("Payment Midtrans", "Midtrans :$tokenmidtrans")
                    val redirecturl = it.midtransResponse.redirectUrl

                    // Cetak log atau tampilkan toast jika perlu
                    Log.d("Payment Redirect", "Redirect URL: $redirectUrl")

                    // Buka redirect URL
                    openRedirectUrl(redirectUrl)



                } else {
                    Toast.makeText(context, "Response Failed", Toast.LENGTH_SHORT).show()
                }

            }


        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment2)
        }


    }

    override fun onResume() {
        super.onResume()
        getDataCart()
    }




    fun getDataCart() {
        val token = pref.getString("token", "").toString()
        cartVm.CartUser(token)
        cartVm.dataCartUser.observe(viewLifecycleOwner, Observer { cartItems ->
            binding.rvListCart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvListCart.adapter = CartAdapter(cartItems,requireContext(), cartVm, idUser, viewLifecycleOwner)


                if (cartItems.isNullOrEmpty()) {
                    // Handle the case when the cart is empty
                    // For example, you can show a message or hide the price view
                    binding.tvPriceProduct.text = "0"
                    // You might want to update other UI elements accordingly
                } else {
                    var totalPrice = 0.0
                    val listOfIds = mutableListOf<String>()
                    val listOfQuantities = mutableListOf<Int>()
                    val listOfTotalPrices = mutableListOf<Int>()

                    for (product in cartItems) {
                        val idProductCart = product.productID.id
                        val productName = product.productID.nameProduct
                        val quantity = product.quantity
                        val price = product.productID.price
                        val keseluruhanharga = quantity * price.toDouble()
                        totalPrice += keseluruhanharga
                        listOfIds.add(idProductCart)
                        listOfQuantities.add(quantity)
                        listOfTotalPrices.add(keseluruhanharga.toInt())
                    }

                    binding.tvPriceProduct.text = totalPrice.toString()


                }









        })


    }


    private fun startRepeatingTask() {
        // Jalankan runnable setiap interval waktu
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Panggil fungsi untuk meminta pengguna untuk login kembali
                promptUserForLogin()

                // Ulangi pengulangan
                handler.postDelayed(this, interval)
            }
        }, interval)
    }

    private fun showLoginDialog() {
        // Tampilkan dialog login menggunakan MaterialAlertDialogBuilder
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Login")
            .setMessage("Anda Belum Login")
            .setCancelable(false)
            .setNegativeButton("Cancel") { dialog, which ->
                // Respon saat tombol negatif ditekan
                // Anda dapat menyesuaikan aksi yang diperlukan di sini
            }
            .setPositiveButton("Login") { dialog, which ->
                // Respon saat tombol positif ditekan
                // Navigasikan pengguna ke layar login
                findNavController().navigate(R.id.action_cartFragment_to_loginFragment)
            }
            .show()
    }

    private fun stopRepeatingTask() {
        // Hentikan pengulangan jika handler belum diinisialisasi
        handler.removeCallbacksAndMessages(null)
    }

    private fun promptUserForLogin() {
        // Cek apakah pengguna telah login
        if (pref.getString("token", "")!!.isEmpty()) {
            // Jika belum login, tampilkan dialog login
            showLoginDialog()
        }
    }




    private fun openRedirectUrl(redirectUrl: String) {
        val bundle = Bundle()

        bundle.putString("URL",redirectUrl).apply {
            findNavController().navigate(R.id.action_cartFragment_to_webViewFragment,bundle)
        }

    }





}

















