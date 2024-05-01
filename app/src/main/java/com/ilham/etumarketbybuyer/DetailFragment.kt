package com.ilham.etumarketbybuyer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ilham.etumarketbybuyer.database.BuyerDatabase
import com.ilham.etumarketbybuyer.database.FavoriteBuyer
import com.ilham.etumarketbybuyer.database.FavoriteBuyerDao
import com.ilham.etumarketbybuyer.databinding.FragmentDetailBinding
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.FavoriteViewModel
import com.ilham.etumarketbybuyer.viewmodel.PaymentViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class DetailFragment : Fragment(), OnMapReadyCallback {
     lateinit var gMap: GoogleMap
    lateinit var binding : FragmentDetailBinding
    lateinit var pref : SharedPreferences
    lateinit var productVm : ProductViewModel
    private lateinit var cartViewModel: CartViewModel
    lateinit var paymentVm : PaymentViewModel
    lateinit var favoriteVm : FavoriteViewModel
    private lateinit var idProduct: String
    private lateinit var idCart: String
    var favDao: FavoriteBuyerDao? = null
    var buyerDb: BuyerDatabase? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)!!
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)
        favoriteVm = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        buyerDb = BuyerDatabase.getInstance(requireContext())
        favDao = buyerDb?.buyerDao()


        val getData =  arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
        val token = pref.getString("token", "").toString()
//        val getId = pref.getString("id", " ")

        val getId = getData.id


        productVm.getproductperid(getId)

        observeDetailProduct()


        val jmlcart = cartViewModel.getCart()

        val belanja = setCart(jmlcart)

        binding.tvJumlahCart.text = belanja.toString()






//        cartViewModel.saveIdCart(idProduct)


        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        binding.favButton.setOnClickListener {
//            checkFavorite = !checkFavorite
//            if (checkFavorite) {
////                addToFavorites()
//            } else {
////                removeFromFavorites()
//            }
//            updateUI()
//        }

        favorite()

        binding.img.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_favoriteFragment)
        }









    }


//    override fun onResume() {
//        super.onResume()
//        val getData = arguments?.getSerializable("detail") as DataAllProduct
//        val id = getData?.id
////        checkButtonFav(id)
//    }

    private fun observeDetailProduct() {
        productVm.dataproductperid.observe(viewLifecycleOwner) { detailproduct ->

            binding.apply {
                if (detailproduct != null) {
                    val boundsBuilder = LatLngBounds.Builder()
                    binding.tvNamaproductdetail.text = detailproduct.data.nameProduct
                    binding.tvHargaproduk.text = detailproduct.data.price.toString()
                    binding.tvDescription.text = detailproduct.data.description
                    binding.tvCategory.text = detailproduct.data.category
                    binding.tvRelease.text = detailproduct.data.releaseDate



                        val lon = detailproduct.data.longitude
                        val lat = detailproduct.data.latitude

                        val latLon = LatLng(lon, lat)
                        Log.d("Detail Rs", "$latLon")

                        gMap.addMarker(
                            MarkerOptions()
                                .position(latLon)
                                .icon(vectorToBitmap(R.drawable.ic_baseline_location_on_24, Color.RED))
                        )

                        boundsBuilder.include(latLon)
                        val bounds: LatLngBounds = boundsBuilder.build()
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))




                    Glide.with(requireContext())
                        .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${detailproduct.data.image}")
                        .into(binding.ivCartimagedetail)

                    val shopName = detailproduct.data.sellerID.shopName
                    getitemshop(shopName)


//                    binding.btnBeliSekarang.setOnClickListener {
//
//
//                        cartViewModel.dataCartUser.observe(viewLifecycleOwner, Observer { cartItems ->
//
//                            if (cartItems.isNullOrEmpty()) {
//                                // Handle the case when the cart is empty
//                                // For example, you can show a message or hide the price view
//                                // You might want to update other UI elements accordingly
//                            } else {
//                                var totalPrice = 0.0
//                                val listOfIds = mutableListOf<String>()
//                                val listOfQuantities = mutableListOf<Int>()
//
//                                for (product in cartItems) {
//                                    val idProductCart = product.productID.id
//                                    val productName = product.productID.nameProduct
//                                    val quantity = product.quantity
//                                    val price = product.productID.price
//                                    val keseluruhanharga = quantity * price.toDouble()
//                                    totalPrice += keseluruhanharga
//                                    listOfIds.add(idProductCart)
//                                    listOfQuantities.add(quantity)
//
//                                }
//
//
//                                val dataCart = PostTransaction(listOfIds, listOfQuantities, totalPrice.toInt())
//                                val token = pref.getString("token", "").toString()
//
//                                paymentVm.postpayment(token, dataCart)
//                            }
//
//
//                        })
//                        paymentVm.midtransResponse.observe(viewLifecycleOwner){
//                            if (it.message == "Transaksi berhasil dibuat") {
//
//                                val tokenmidtrans = it.midtransResponse.token
//                                val redirectUrl = it.midtransResponse.redirectUrl
//
//
//                                Log.d("Payment Midtrans", "Midtrans :$tokenmidtrans")
//                                val redirecturl = it.midtransResponse.redirectUrl
//
//                                // Cetak log atau tampilkan toast jika perlu
//                                Log.d("Payment Redirect", "Redirect URL: $redirectUrl")
//
//                                // Buka redirect URL
//                                openRedirectUrl(redirectUrl)
//
//
//
//                            } else {
//                                Toast.makeText(context, "Response Failed", Toast.LENGTH_SHORT).show()
//                            }
//
//                        }
//
//
//
//
//
//
//
//                    }






                }

            }





            getPostCart()
        }
    }


    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addToCart() {
        val getData =  arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
        val getid = getData.id

        val jmlcart = cartViewModel.getCart()

        val belanja = setCart(jmlcart)

        binding.tvJumlahCart.text = belanja.toString()

        val dataCart = DataAddCart(listOf(getid), listOf(belanja))
        val token = pref.getString("token", "").toString()
        cartViewModel.postCart(token,dataCart)



    }

    private fun addItemCart() {


        productVm.dataproductperid.observe(viewLifecycleOwner) {

            addToCart()
           
//            addToCart(idUser, name, productImage, price, desc)
            if (it.message == "Get product by id") {
//                cartViewModel.getIdCart()
//                val idseller =  it.data.id
//                cartViewModel.saveIdCart(idseller)

                val sharedPref = pref.edit()
                sharedPref.putString("idbuyer", it.data.sellerID.id)
                sharedPref.apply()
                findNavController().navigate(R.id.action_detailFragment_to_cartFragment)
                Toast.makeText(requireContext(), "Berhasil menambahkan ke cart", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Gagal menambahkan ke cart", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setCart(Cart:Int):Int {
        binding.tvJumlahCart.text = Cart.toString()
        var Belanja = Cart

        binding.addJumlahCart.setOnClickListener {
            Belanja += 1
            binding.tvJumlahCart.text = Belanja.toString()
        }

        binding.decJumlahCart.setOnClickListener {
            if (Belanja > 0) {
               Belanja -= 1
                binding.tvJumlahCart.text = Belanja.toString()
            }
        }
        return Belanja
    }

    private fun getPostCart() {
        binding.icCart.apply {
            setOnClickListener {
                val tvDewasa = binding.tvJumlahCart.text.toString()
                cartViewModel.savecartPreferences(tvDewasa.toInt())
                addItemCart()
//                cartViewModel.getIdCart()
//                findNavController().navigate(R.id.action_detailProductFragment_to_cartFragment)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style
            )
        )
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,) == PackageManager.PERMISSION_GRANTED
        ) {
            gMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun getitemshop(search: String){
        productVm.getproductpershop(search)
        productVm.datapershop.observe(viewLifecycleOwner){
            val layoutManager = GridLayoutManager(context,2)
            binding.rvListDetail.layoutManager = layoutManager
            if (it!= null) {
                binding.rvListDetail.adapter = ShopAdapter(it)
            }

        }
    }

    private fun openRedirectUrl(redirectUrl: String) {
        val bundle = Bundle()

        bundle.putString("URL",redirectUrl).apply {
            findNavController().navigate(R.id.action_detailFragment_to_webViewFragment,bundle)
        }

    }




//    private fun updateUI() {
//        if (checkFavorite) {
//            binding.favButton.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
//        } else {
//            binding.favButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
//        }
//        binding.favButton.isChecked = checkFavorite
//    }



//    private fun updateButton(checkResult: Int) {
//        if (checkResult > 0) {
//            binding.favButton.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
//            binding.favButton.isChecked = true
//            checkFavorite = true
//        } else {
//            binding.favButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
//            binding.favButton.isChecked = false
//            checkFavorite = false
//        }
//    }

    private fun favorite(){
        binding.favButton.setOnClickListener {
            GlobalScope.async {
                val getFav = arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
                val id = getFav.id
                val category = getFav.category
                val description = getFav.description
                val image = getFav.image
                val latitude = getFav.latitude
                val longitude = getFav.longitude
                val namaproduk = getFav.nameProduct
                val price = getFav.price
                val releseDate = getFav.releaseDate
                val hasil = buyerDb?.buyerDao()!!.addToFavorit(FavoriteBuyer(id,category,description,image,latitude,longitude, namaproduk, price, releseDate))

                activity?.runOnUiThread {
                    if (hasil != 0.toLong()){
                        Toast.makeText(context, "Add to Favorit", Toast.LENGTH_LONG).show()
                        var _isChecked = false
                        _isChecked = !_isChecked
                        binding.favButton.isChecked = _isChecked


                    }else{
                        Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                    }
                }






            }
        }
    }


}


