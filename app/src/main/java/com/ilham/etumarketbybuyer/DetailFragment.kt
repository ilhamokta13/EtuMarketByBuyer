package com.ilham.etumarketbybuyer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.ilham.etumarketbybuyer.database.BuyerDatabase
import com.ilham.etumarketbybuyer.database.FavoriteBuyer
import com.ilham.etumarketbybuyer.database.FavoriteBuyerDao
import com.ilham.etumarketbybuyer.databinding.FragmentDetailBinding
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.chat.User
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.FavoriteViewModel
import com.ilham.etumarketbybuyer.viewmodel.PaymentViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*

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
    val listuser = ArrayList<User>()
    lateinit var firebaseAuth: FirebaseAuth


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
        //Mengambil instance dari SharedPreferences dengan nama "Berhasil" dalam mode privat.
        // SharedPreferences digunakan untuk menyimpan dan mengambil data sederhana seperti pasangan nilai kunci.
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)!!
        //Menginisialisasi beberapa ViewModel untuk digunakan dalam fragment ini. ViewModel adalah komponen arsitektur Android yang dirancang untuk menyimpan
        // dan mengelola data UI secara terpisah dari lifecycle.
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)
        favoriteVm = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()

        //Mengambil instance dari BuyerDatabase dan buyerDao.
        // Ini digunakan untuk mengakses data pembeli dalam basis data lokal.

        buyerDb = BuyerDatabase.getInstance(requireContext())
        favDao = buyerDb?.buyerDao()

        //Mengambil data produk yang dikirim sebagai argumen ke fragment ini.
        // Data tersebut kemudian digunakan untuk mengambil ID produk dan token dari SharedPreferences.
        val getData =  arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
        val token = pref.getString("token", "").toString()
//        val getId = pref.getString("id", " ")

        val getId = getData.id
        //Memanggil fungsi getproductperid di ProductViewModel untuk mendapatkan detail produk berdasarkan ID
        // dan mengamati perubahan data detail produk dengan observeDetailProduct.
        productVm.getproductperid(getId)

        observeDetailProduct()

        //Mengambil jumlah item di keranjang belanja dari CartViewModel dan
        // mengatur teks di UI untuk menampilkan jumlah tersebut.
        val jmlcart = cartViewModel.getCart()
        val belanja = setCart(jmlcart)
        binding.tvJumlahCart.text = belanja.toString()






//        cartViewModel.saveIdCart(idProduct)

        //Mengambil fragment peta dari layout dan mengatur callback getMapAsync untuk inisialisasi peta.
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

        binding.chatpenjual.setOnClickListener {
            val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

            var userid = firebase.uid
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Users")

            val user = Firebase.auth.currentUser
            user?.let {
                // Name, email address, and profile photo Url
                val name = it.displayName
                val email = it.email
                val photoUrl = it.photoUrl

                // Check if user's email is verified
                val emailVerified = it.isEmailVerified

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
//                val uid = it.uid

                val hashMap: HashMap<String, String> = HashMap()
                hashMap.get("fullname")

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("userId", userid)
                intent.putExtra("fullname", name)

                Log.d("Usernamerek", "Usernamenya benar $name")
                Log.d("userIdrek", "Idnya benar $userid")
                startActivity(intent)


            }


        }





//        binding.shareloc.setOnClickListener {
////            getLocation()
//        }

        initializeFirebaseMessaging()











    }


//    override fun onResume() {
//        super.onResume()
//        val getData = arguments?.getSerializable("detail") as DataAllProduct
//        val id = getData?.id
////        checkButtonFav(id)
//    }


    private fun initializeFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        // Lakukan sesuatu dengan token jika perlu
                    }
                }
            }
    }

    private fun observeDetailProduct() {
        productVm.dataproductperid.observe(viewLifecycleOwner) { detailproduct ->

            binding.apply {
                if (detailproduct != null) {
                    val boundsBuilder = LatLngBounds.Builder()
                    binding.tvNamaproductdetail.text = detailproduct.data.nameProduct
                    val hargaproduk  = detailproduct.data.price.toString()
                    binding.tvHargaproduk.text = "Harga Fix : $hargaproduk"
                    binding.tvDescription.text = detailproduct.data.description
                    binding.tvCategory.text = detailproduct.data.category
                    binding.tvRelease.text = detailproduct.data.releaseDate
                    binding.tvNamatoko.text = detailproduct.data.sellerID.shopName
                    binding.tvPemilikToko.text = detailproduct.data.sellerID.fullName
                    binding.tvStock.text = detailproduct.data.stock.toString()


                   // Mengambil nilai longitude dan latitude dari produk untuk membuat LatLng dan menambahkan marker pada Google Map (gMap).

                        val lon = detailproduct.data.longitude
                        val lat = detailproduct.data.latitude

                        val latLon = LatLng(lat,lon)
                        Log.d("Detail Rs", "$latLon")

                    //Menyertakan lokasi marker ke dalam bounds builder dan menyesuaikan tampilan kamera peta agar marker terlihat dengan jelas

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


                }

            }
            getPostCart()



            binding.tawar.setOnClickListener {
                val bundle = Bundle()
                val id = detailproduct.data.id
                bundle.putString("tawar", id)
                findNavController().navigate(R.id.action_detailFragment_to_tawarHargaFragment, bundle)

            }

        }
    }

    //Fungsi vectorToBitmap ini mengubah drawable vektor menjadi BitmapDescriptor yang dapat digunakan sebagai ikon kustom untuk marker pada Google Maps.
    //Fungsi ini bernama vectorToBitmap dan menerima dua parameter: id yang merupakan ID dari drawable resource dan color yang merupakan warna dalam bentuk integer
    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        //Menggunakan ResourcesCompat.getDrawable untuk mendapatkan drawable vektor dari resources dengan ID yang diberikan. Jika drawable tidak ditemukan, fungsi akan mencatat pesan error dan mengembalikan marker default.
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        //Membuat objek Bitmap dengan ukuran yang sesuai dengan dimensi asli dari drawable vektor. Bitmap ini akan digunakan untuk menggambar drawable vektor
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        //Membuat Canvas dari bitmap yang baru dibuat. Mengatur batas drawable vektor agar sesuai dengan ukuran canvas, kemudian menetapkan warna (tint) untuk drawable vektor menggunakan DrawableCompat.setTint. Terakhir, menggambar drawable vektor ke canvas.
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        //Menggunakan BitmapDescriptorFactory.fromBitmap untuk mengubah bitmap menjadi BitmapDescriptor, yang kemudian dikembalikan oleh fungsi in
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addToCart(latitude : Double, longitude:Double) {
        //Mengambil objek DataAllProduct dari argumen dengan kunci "detail". Kemudian, mengambil id produk dari objek tersebut

        val getData = arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
        val getid = getData.id
        //Mengambil jumlah barang dalam keranjang belanja dengan memanggil cartViewModel.getCart(), lalu memprosesnya dengan fungsi setCart (tidak didefinisikan dalam kode ini, tetapi kemungkinan mengembalikan jumlah barang yang diperbarui).
        // Kemudian, memperbarui tampilan teks tvJumlahCart dengan jumlah barang dalam keranjang.

        val jmlcart = cartViewModel.getCart()
        val belanja = setCart(jmlcart)
        binding.tvJumlahCart.text = belanja.toString()
        //Membuat objek DataAddCart yang berisi informasi lokasi (latitude dan longitude), daftar ID produk yang akan ditambahkan ke keranjang (hanya satu ID dalam bentuk list),
        // dan daftar jumlah barang dalam keranjang (hanya satu jumlah dalam bentuk list)

        val dataCart = DataAddCart(latitude, longitude,listOf(getid), listOf(belanja))
        val token = pref.getString("token", "").toString()
        cartViewModel.postCart(token, dataCart)
    }

    private fun addToCartNoLocation() {
        //Mengambil objek DataAllProduct dari argumen dengan kunci "detail".
        // Kemudian, mengambil id produk dari objek tersebut
        val getData =  arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
        val getid = getData.id

        //Mengambil jumlah barang dalam keranjang belanja dengan memanggil cartViewModel.getCart(), lalu memprosesnya dengan fungsi setCart (tidak didefinisikan dalam kode ini, tetapi kemungkinan mengembalikan jumlah barang yang diperbarui).
        // Kemudian, memperbarui tampilan teks tvJumlahCart dengan jumlah barang dalam keranjang

        val jmlcart = cartViewModel.getCart()

        val belanja = setCart(jmlcart)

        binding.tvJumlahCart.text = belanja.toString()

        //Membuat objek DataAddCart yang berisi informasi lokasi (latitude dan longitude diset ke 0.0), daftar ID produk yang akan ditambahkan ke keranjang (hanya satu ID dalam bentuk list),
        // dan daftar jumlah barang dalam keranjang (hanya satu jumlah dalam bentuk list).
        val dataCart = DataAddCart(0.0, 0.0,listOf(getid), listOf(belanja))
        //Mengambil token autentikasi dari shared preferences dengan kunci "token". Kemudian, memanggil cartViewModel.postCart
        // dengan token dan objek dataCart untuk mengirim data keranjang belanja ke server atau database
        val token = pref.getString("token", "").toString()
        cartViewModel.postCart(token,dataCart)

    }

    private fun getLocationAndAddToCart() {
        //Mendapatkan instance FusedLocationProviderClient untuk mengakses layanan lokasi dari Google Play Services
        val fusedLocProvClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //Memeriksa apakah aplikasi memiliki izin untuk mengakses lokasi pengguna.
        // Jika tidak, meminta izin tersebut dari pengguna
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 10
            )
        } else {
            //Jika izin lokasi sudah diberikan,
            // mencoba mendapatkan lokasi terakhir yang diketahui menggunakan fusedLocProvClient
            fusedLocProvClient.lastLocation.addOnSuccessListener { location ->
                //Jika lokasi tidak null, mengambil nilai latitude dan longitude dari lokasi tersebut dan
                // memanggil fungsi addToCart dengan koordinat tersebut untuk menambahkan produk ke keranjang belanja.
                if (location != null) {
                    val longitude = location.longitude
                    val latitude = location.latitude
                    addToCart(latitude, longitude) // Add to cart with current location
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) as List<Address>
                        //Menggunakan Geocoder untuk mendapatkan alamat dari koordinat lokasi pengguna. Jika berhasil,
                        // mengambil alamat pertama dari daftar hasil dan menyimpannya menggunakan productVm.saveLock.
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            productVm.saveLock(locName)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    //Jika lokasi null, menampilkan pesan Toast yang memberitahukan pengguna untuk menyalakan Maps.
                    // Jika terjadi kegagalan dalam mendapatkan lokasi, menampilkan pesan error menggunakan Toast.
                   Toast.makeText(context, "Jika anda ingin produk diantar, nyalakan maps", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun getLocation() {
        val fusedLocProvClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 10
            )
        } else {
            fusedLocProvClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude= location.latitude.toString()
                    val longitude = location.longitude.toString()
                    val altitude = location.altitude.toString()
                   val edacc = "${location.accuracy}%"

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            productVm.saveLock(locName)


                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addItemCart() {


        productVm.dataproductperid.observe(viewLifecycleOwner) {

            if (it.data.stock > 0) {
                getLocationAndAddToCart()
                addToCartNoLocation()
                val sharedPref = pref.edit()
                sharedPref.putString("idbuyer", it.data.sellerID.id)
                sharedPref.apply()
                findNavController().navigate(R.id.action_detailFragment_to_cartFragment)
                Toast.makeText(requireContext(), "Berhasil menambahkan ke cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Stock produk habis", Toast.LENGTH_SHORT).show()
            }
        }


        }


    //Fungsi setCart mengatur jumlah item dalam keranjang belanja. Nilai awal Cart ditampilkan, dan pengguna dapat menambah atau mengurangi jumlah item menggunakan tombol addJumlahCart dan decJumlahCart.
    // Jumlah yang diperbarui ditampilkan kembali dan dikembalikan oleh fungsi ini.

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

    //Fungsi getPostCart menetapkan OnClickListener pada ikon keranjang (icCart). Ketika diklik, jumlah item dalam keranjang disimpan dalam ViewModel (cartViewModel.savecartPreferences)
    // dan item ditambahkan ke keranjang dengan memanggil fungsi addItemCart
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

    //Fungsi onMapReady menginisialisasi peta (gMap) dengan gaya khusus, mengaktifkan beberapa kontrol UI,
    // dan memanggil fungsi getMyLocation untuk mendapatkan lokasi pengguna.

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

    //requestPermissionLauncher digunakan untuk meminta izin lokasi dari pengguna.
    // Jika izin diberikan, fungsi getMyLocation dipanggil

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    //Fungsi getMyLocation memeriksa apakah izin lokasi telah diberikan.
    // Jika ya, fitur lokasi pengguna diaktifkan pada peta (gMap). Jika tidak, permintaan izin diajukan

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,) == PackageManager.PERMISSION_GRANTED
        ) {
            gMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //Fungsi getitemshop mengambil produk berdasarkan nama toko (search). Data yang diperoleh diamati dan ditampilkan dalam
    // RecyclerView (rvListDetail) menggunakan GridLayoutManager dan adapter ShopAdapter.

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


