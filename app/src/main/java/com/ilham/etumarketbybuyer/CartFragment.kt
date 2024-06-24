package com.ilham.etumarketbybuyer

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilham.etumarketbybuyer.databinding.FragmentCartBinding
import com.ilham.etumarketbybuyer.model.cart.usercart.CartProduct
import com.ilham.etumarketbybuyer.model.cart.usercart.Data
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.transaksi.Destination
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.lang.Byte.decode
import android.util.Base64
import com.ilham.etumarketbybuyer.model.chat.NotificationData
import com.ilham.etumarketbybuyer.model.chat.PushNotification
import com.ilham.etumarketbybuyer.model.chat.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Integer.decode
import java.security.spec.PSSParameterSpec.DEFAULT
import java.util.*

//Annotation: @AndroidEntryPoint menandakan bahwa
// Hilt digunakan untuk injeksi dependensi dalam Fragment ini.
@AndroidEntryPoint
class CartFragment : Fragment()  {
    lateinit var binding: FragmentCartBinding
    lateinit var pref: SharedPreferences
    lateinit var cartVm: CartViewModel
    lateinit var cartadapter: CartAdapter
    lateinit var productVm: ProductViewModel
    lateinit var paymentVm : PaymentViewModel
    lateinit var userVm : UserViewModel
    lateinit var historyVm : HistoryViewModel
    private lateinit var idProduct: String
    private var itemcarts: List<CartProduct> = listOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        cartVm = ViewModelProvider(this).get(CartViewModel::class.java)
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        //Inisialisasi: Menginisialisasi SharedPreferences, ViewModelProvider, dan CartAdapter.
        //Pengaturan RecyclerView: Mengatur RecyclerView dengan LinearLayoutManager dan adaptor.

        val token = pref.getString("token", "").toString()
        cartadapter = CartAdapter(itemcarts, requireContext(), cartVm, token, viewLifecycleOwner)

        binding.rvListCart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvListCart.adapter = cartadapter

        //validasi Token: Memeriksa apakah token kosong atau sudah kadaluarsa.
        // Jika ya, sembunyikan RecyclerView dan tampilkan dialog untuk meminta pengguna login ulang.
        if (token.isEmpty() || isTokenExpired(token)) {
            binding.rvListCart.visibility = View.GONE
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Login")
                .setMessage("Your session has expired. Please login again.")
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
        } else {
            getDataCart()
        }

        //Tombol Checkout: Mengatur logika untuk tombol checkout, termasuk mengamati LiveData dari cartVm dan paymentVm,
        // menghitung total harga, dan memproses transaksi serta mengirim notifikasi.
        binding.btnCheckout.setOnClickListener {
            //Fungsi ini menggunakan LiveData observer untuk memantau perubahan pada data keranjang belanja (dataCartUser) dari cartVm (ViewModel).
            // Setiap kali data keranjang belanja berubah, blok kode dalam observer akan dieksekusi.
            cartVm.dataCartUser.observe(viewLifecycleOwner, Observer { cartItems ->
                //RecyclerView (rvListCart) diatur dengan LinearLayoutManager untuk menampilkan item secara vertikal, dan adapter (cartadapter) diatur untuk menampilkan item dalam keranjang belanja.
                binding.rvListCart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.rvListCart.adapter = cartadapter
                //Jika keranjang belanja kosong, teks harga produk (tvPriceProduct) diatur menjadi "0" dan pesan log "Cart is empty" dicetak.
                // Jika tidak kosong, program melanjutkan untuk menghitung total harga dan biaya pengiriman.
                if (cartItems.isNullOrEmpty()) {
                    // Handle the case when the cart is empty
                    binding.tvPriceProduct.text = "0"
                    Log.d("CartFragment", "Cart is empty")
                } else {
                    //Program menghitung total harga produk (totalPrice) dan biaya pengiriman (shippingCost). ID produk dan kuantitas disimpan dalam daftar (listOfIds dan listOfQuantities).
                    // Total harga produk dan biaya pengiriman kemudian ditampilkan pada UI.
                    var totalPrice = 0.0
                    var shippingCost = 0
                    val listOfIds = mutableListOf<String>()
                    val listOfQuantities = mutableListOf<Int>()

                    // Collect product IDs and quantities
                    for (cartItem in cartItems) {
                        val idProductCart = cartItem.product.productID.id
                        val quantity = cartItem.product.quantity
                        val price = cartItem.product.productID.price
                        val totalProductPrice = quantity * price.toDouble()
                        totalPrice += totalProductPrice
                        listOfIds.add(idProductCart)
                        listOfQuantities.add(quantity)

                        // Assuming shipping cost is the same for all items
                        shippingCost = cartItem.shippingCost
                    }

                    val totalCost = totalPrice + shippingCost
                    binding.txttotalproduk.text = totalPrice.toString()
                    binding.txtshippingCost.text = shippingCost.toString()
                    binding.tvPriceProduct.text = totalCost.toString()

                    // Proceed to checkout without location information
                    //Program menampilkan dialog konfirmasi untuk melanjutkan ke proses checkout.
                    // Jika pengguna memilih "Yes", program akan membuat objek PostTransaction dengan data keranjang dan
                    // mengirimkan permintaan pembayaran menggunakan paymentVm.postpayment.
                    // Jika pengguna memilih "No", program akan menavigasi ke homeFragment2.
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirmation")
                        .setMessage("Do you want to proceed with checkout?")
                        .setPositiveButton("Yes") { _, _ ->
                            // Perform checkout without location information
                            val destination = Destination(0.0, 0.0) // Dummy destination or any default location
                            val dataCart = PostTransaction(destination, listOfIds, listOfQuantities, totalPrice.toInt())
                            val token = pref.getString("token", "").toString()

                            paymentVm.postpayment(token, dataCart)
                        }
                        .setNegativeButton("No") { _, _ ->
                            findNavController().navigate(R.id.action_cartFragment_to_homeFragment2)
                        }
                        .show()
                }
            })

            paymentVm.midtransResponse.observe(viewLifecycleOwner) {
                if (it.message == "Transaksi berhasil dibuat") {
                    val tokenmidtrans = it.midtransResponse.token
                    val redirectUrl = it.midtransResponse.redirectUrl

                    Log.d("Payment Midtrans", "Midtrans: $tokenmidtrans")
                    Log.d("Payment Redirect", "Redirect URL: $redirectUrl")

                    openRedirectUrl(redirectUrl)

                    // Kirim notifikasi setelah transaksi berhasil
                    val notificationData = NotificationData(
                        title = "Transaksi Berhasil",
                        message = "Transaksi Anda dengan ID $tokenmidtrans telah berhasil."
                    )
                    val pushNotification = PushNotification(
                        data = notificationData,
                        to = "/topics/your_topic" // Sesuaikan dengan topik atau penerima notifikasi
                    )

                    sendNotification(pushNotification)

                } else {
                    Toast.makeText(context, "Response Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }



//



        //Tombol Delete: Menghapus item dari keranjang dan memperbarui tampilan.
        //Tombol Back: Menavigasi kembali ke halaman utama.
        binding.btnDeletecart.setOnClickListener {
            cartVm.deletecart(token)
            Toast.makeText(requireContext(), "Item telah dihapus", Toast.LENGTH_SHORT).show()
            cartadapter.notifyDataSetChanged()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment2)
        }




    }



    //onResume: Memanggil getDataCart setiap kali Fragment aktif kembali.
    override fun onResume() {
        super.onResume()

        getDataCart()

    }

    private fun getDataCart(){
        val token = pref.getString("token", "").toString()
        cartVm.CartUser(token)
        Log.d("CartFragment", "Fetching cart data with token: $token")

        cartVm.dataCartUser.observe(viewLifecycleOwner, Observer { newCartItems ->
            Log.d("CartFragment", "Received cart items: $newCartItems")
            if (newCartItems != null) {
                cartadapter.updateCartItems(newCartItems)
                Log.d("CartFragment", "CartAdapter updated with new items")
            }
            if (newCartItems.isNullOrEmpty()) {
                // Handle the case when the cart is empty
                binding.tvPriceProduct.text = "0"
                Log.d("CartFragment", "Cart is empty")
            } else {
                var totalPrice = 0.0
                var shippingCost = 0
                val listOfIds = mutableListOf<String>()
                val listOfQuantities = mutableListOf<Int>()
                var latitude: Double? = null
                var longitude: Double? = null

                for (product in newCartItems) {
                    if (latitude == null && longitude == null) {
                        latitude = product.latitude
                        longitude = product.longitude

                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            // Define valid ranges for latitude and longitude
                            val MIN_LATITUDE = -90.0
                            val MAX_LATITUDE = 90.0
                            val MIN_LONGITUDE = -180.0
                            val MAX_LONGITUDE = 180.0

                            // Validate latitude and longitude values
                            if (latitude !in MIN_LATITUDE..MAX_LATITUDE) {
                                throw IllegalArgumentException("Latitude is out of range: $latitude")
                            }
                            if (longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
                                throw IllegalArgumentException("Longitude is out of range: $longitude")
                            }

                            // Proceed with geocoding if values are valid
                            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
                            if (addresses.isNotEmpty()) {
                                val locName: String = addresses[0].getAddressLine(0)
                                binding.LokasiPengiriman.text = "Lokasi Pengiriman : $locName"
                                productVm.saveLoc(locName)
                            }
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                            // Handle invalid latitude or longitude error
                            // For example, show an error message to the user
                            binding.LokasiPengiriman.text = "Invalid latitude or longitude."
                        } catch (e: IOException) {
                            e.printStackTrace()
                            // Handle other IOExceptions
                        }

                    }

                    val idProductCart = product.product.productID.id
                    shippingCost = product.shippingCost
                    val quantity = product.product.quantity
                    val price = product.product.productID.price
                    val totalProductPrice = quantity * price.toDouble()
                    totalPrice += totalProductPrice
                    listOfIds.add(idProductCart)
                    listOfQuantities.add(quantity)
                }

                val totalCost = totalPrice + shippingCost
                binding.tvPriceProduct.text = totalCost.toString()
                binding.txttotalproduk.text = "Harga Total Produk : $totalPrice"
                binding.txtshippingCost.text = "Biaya Ongkir : $shippingCost"


                // Update adapter after receiving data
                cartadapter.updateCartItems(newCartItems)
            }
        })
    }


    fun isTokenExpired(token: String): Boolean {
        try {
            val split = token.split(".")
            val decodedBytes = Base64.decode(split[1], Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(decodedString)
            val exp = jsonObject.getLong("exp")
            val currentTime = System.currentTimeMillis() / 1000
            return currentTime > exp
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true // If there's an error decoding the token, assume it's expired.
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.IO) {

                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d("TAG", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("TAG", response.errorBody()!!.string())
                }


            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }















    private fun updateTotalPrice(cartData: List<Data>) {
        val totalPrice = cartData.flatMap { it.products }
            .sumOf { it.quantity * it.productID.price } + cartData.sumOf { it.shippingCost }

    }






    private fun openRedirectUrl(redirectUrl: String) {
        val bundle = Bundle().apply {
            putString("URL", redirectUrl)
        }
        findNavController().navigate(R.id.action_cartFragment_to_webViewFragment, bundle)
    }







}




















