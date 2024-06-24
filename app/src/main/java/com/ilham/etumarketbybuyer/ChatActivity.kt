package com.ilham.etumarketbybuyer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.*
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.ilham.etumarketbybuyer.databinding.ActivityChatBinding
import com.ilham.etumarketbybuyer.model.chat.*
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class ChatActivity : AppCompatActivity(),OnMapReadyCallback {
    lateinit var binding: ActivityChatBinding
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    var topic = ""
    private lateinit var pref: SharedPreferences
    private var selectedImagePath: String? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private lateinit var locationManager: LocationManager
    lateinit var productVm : ProductViewModel
    lateinit var gMap: GoogleMap



    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val NOTIFICATION_ID = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
        private const val MAPS_REQUEST_CODE = 5678
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        FirebaseApp.initializeApp(this)


        var intent = intent
        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("fullname")

        Log.d("Username", "Username benar $userName")
        Log.d("userId", "Id benar $userId")





        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key))
        }


        binding.tvUserName.text = userName
        Glide.with(applicationContext)
            .load(R.drawable.profile_image)
            .into(binding.imgProfile)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)



        binding.btnSendImage.setOnClickListener {
            openImagePicker()
        }


        binding.btnSendMessage.setOnClickListener {
            sendMessageWithOptionalLocationAndImage()
        }


        readMessage(firebaseUser!!.uid, userId)

        binding.btnSendLocation.setOnClickListener {
            getLocation()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImagePath = data.data?.toString()
            showImagePreview(selectedImagePath)
        }
    }


    private fun sendMessageWithOptionalLocationAndImage() {
        //Pertama, fungsi mengambil informasi tentang penerima pesan (userId) dan nama pengguna pengirim (userName) dari intent yang dikirimkan ke aktivitas ini.
        // Selain itu, pesan yang akan dikirim juga diambil dari teks yang dimasukkan oleh pengguna.
        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("fullname")
//
//        val userId = "vd2FrKFAMxYaHBQsn1RJsjZo6cg1"
//        val userName = "Akhrisna"
        val message: String = binding.etMessage.text.toString().trim()

        //Fungsi ini memeriksa apakah lokasi pengguna saat ini tersedia (currentLatitude dan currentLongitude). Jika lokasi tersedia, fungsi akan membuat URL Google Maps dengan koordinat tersebut dan mengirim pesan dengan URL tersebut ke penerima pesan.
        // Setelah itu, teks input dihapus dan koordinat pengguna disetel kembali ke null.

        Log.d("ChatActivity", "sendMessageWithOptionalLocationAndImage called")
        Log.d("ChatActivity", "currentLatitude: $currentLatitude, currentLongitude: $currentLongitude, selectedImagePath: $selectedImagePath")

        // Jika lokasi tersedia, kirim pesan lokasi
        if (currentLatitude != null && currentLongitude != null) {
            val mapsUrl = "https://www.google.com/maps/search/?api=1&query=$currentLatitude,$currentLongitude"
            sendMessage(firebaseUser!!.uid, userId!!, mapsUrl)
            binding.etMessage.setText("")
            currentLatitude = null
            currentLongitude = null
            Log.d("ChatActivity", "Location message sent: $mapsUrl")

        }
        // Jika ada gambar yang dipilih, upload gambar dan kirim pesan
        else if (!selectedImagePath.isNullOrEmpty()) {
            uploadImage(message)
            Log.d("ChatActivity", "Image upload initiated")
        }
        // Jika hanya pesan teks yang tersedia, kirim pesan teks
        else if (message.isNotEmpty()) {
            sendMessage(firebaseUser!!.uid, userId!!, message)
            binding.etMessage.setText("")
            Log.d("ChatActivity", "Text message sent: $message")
        }

        selectedImagePath = null
        topic = "/topics/$userId"

        // Jika ada pesan yang dikirim atau gambar yang dipilih, kirim notifikasi
        if (!message.isNullOrEmpty() || !selectedImagePath.isNullOrEmpty()) {
            PushNotification(
                NotificationData(userName!!, message),
                topic
            ).also {
                sendNotification(it)
                Log.d("ChatActivity", "Notification sent")
            }
        }
        //Menutup Tampilan Preview Gambar: Terakhir, tampilan preview gambar ditutup.
        binding.layoutImagePreview.visibility = View.GONE
    }

    // Fungsi openImagePicker() yang disediakan secara terpisah digunakan
    // untuk membuka galeri gambar agar pengguna dapat memilih gambar untuk diunggah dan dikirim.
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }


    // Add this constant
    private val IMAGE_PICK_REQUEST = 101

    //Fungsi uploadImage() bertanggung jawab untuk mengunggah gambar yang dipilih oleh pengguna ke penyimpanan cloud,
    // dan kemudian mengirim pesan dengan URL gambar tersebut kepada penerima pesan.
    private fun uploadImage(message: String) {
        //Mengambil Data: Pertama, fungsi mengambil informasi tentang penerima pesan (userId) dari intent yang dikirimkan ke aktivitas ini. Selain itu, sebuah referensi ke Firebase Storage dibuat untuk menyimpan gambar yang akan diunggah.
        // Nama file gambar dibuat dengan menggunakan System.currentTimeMillis() untuk memastikan nama unik.
        val userId = intent.getStringExtra("userId")
        val storageRef = FirebaseStorage.getInstance().reference.child("chat_images/${System.currentTimeMillis()}")
        val imageUri = Uri.parse(selectedImagePath)

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Kirim pesan dengan URL gambar (dan teks jika ada)
                    sendMessage(firebaseUser!!.uid, userId!!, message, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error uploading image: ${e.message}")
            }
    }


    private fun getLocation() {
        //FusedLocationProviderClient digunakan untuk mendapatkan data lokasi terbaru.
        val fusedLocProvClient = LocationServices.getFusedLocationProviderClient(this)
        //Objek ini digunakan untuk membuat batasan geografis berdasarkan koordinat.
        val boundsBuilder = LatLngBounds.Builder()
        //Program memeriksa apakah aplikasi memiliki izin untuk mengakses lokasi yang akurat (ACCESS_FINE_LOCATION) atau kasar (ACCESS_COARSE_LOCATION). Jika tidak, dan versi SDK adalah Marshmallow atau lebih tinggi,
        // aplikasi akan meminta izin tersebut dari pengguna.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            //Jika izin sudah diberikan, program akan mencoba mendapatkan lokasi terakhir yang diketahui.
            fusedLocProvClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    //Menyimpan koordinat latitude dan longitude dalam variabel currentLatitude dan currentLongitude.
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    // Display the location on the map
                    //Memanggil fungsi displayLocationOnMap untuk menampilkan lokasi pada peta.
                    displayLocationOnMap(currentLatitude!!, currentLongitude!!)
                    //Menggunakan Geocoder untuk mendapatkan alamat yang lebih rinci berdasarkan koordinat tersebut
                    // dan menyimpannya menggunakan productVm.saveLocation(locName).
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            productVm.saveLocation(locName)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
                }
                //Jika ada kegagalan saat mencoba mendapatkan lokasi,
            // program akan menampilkan pesan kesalahan.
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String, imageUrl: String? = null, latitude: Double?= null, longitude: Double?=null) {
        //Program menginisialisasi referensi ke database Firebase Realtime Database.
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
        //Membuat HashMap untuk Menyimpan Data Pesan:
        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)
        //Program membuat sebuah objek Chat dengan parameter yang diberikan. Objek ini mencakup ID pengirim, ID penerima, pesan, URL gambar (jika ada),
        // serta koordinat latitude dan longitude (jika ada).
        val chat = Chat(senderId, receiverId, message, imageUrl, latitude, longitude)
        //Program menambahkan pesan ke dalam node "Chat" di Firebase Realtime Database. Metode push() digunakan untuk membuat node baru dengan ID unik di bawah "Chat", dan
        // setValue(chat) digunakan untuk menyimpan objek Chat tersebut di node baru ini.

        reference!!.child("Chat").push().setValue(chat)

    }



    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")
        //Dalam blok ini, sebuah ValueEventListener ditambahkan ke referensi database.
        // ValueEventListener memiliki dua metode utama:

        databaseReference.addValueEventListener(object : ValueEventListener {
            //onCancelled(error: DatabaseError): Metode ini akan dipanggil jika ada kesalahan saat mencoba membaca data dari database.
            // Di sini, metode ini belum diimplementasikan (menunjukkan penggunaan todo).
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            //onDataChange(snapshot: DataSnapshot): Metode ini akan dipanggil setiap kali data
            // dalam node "Chat" berubah.
            override fun onDataChange(snapshot: DataSnapshot) {
                //Dalam metode onDataChange, pertama-tama program membersihkan chatList yang mungkin berisi pesan dari iterasi sebelumnya. Kemudian, program memeriksa setiap snapshot anak dari snapshot utama. Jika pesan memenuhi kondisi bahwa salah satu pengguna adalah pengirim dan yang lainnya adalah penerima,
                // pesan tersebut ditambahkan ke dalam chatList.
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                //Setelah chatList diperbarui dengan pesan yang sesuai, sebuah ChatAdapter baru dibuat dan diatur sebagai adapter
                // untuk RecyclerView yang menampilkan pesan-pesan tersebut.
                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                binding.chatRecyclerView.adapter = chatAdapter
            }
        })
    }

    //Fungsi ini dijalankan di dalam coroutine yang menggunakan Dispatchers.IO, yang berarti tugas ini akan dijalankan pada thread yang dioptimalkan untuk
    // operasi I/O (Input/Output), seperti panggilan jaringan.
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
          //Blok withContext juga menggunakan Dispatchers.IO untuk memastikan bahwa semua operasi jaringan dan I/O dilakukan di thread yang tepat.
            withContext(Dispatchers.IO) {
                //Program membuat panggilan jaringan untuk mengirim notifikasi dengan menggunakan Retrofit. Jika panggilan berhasil, program akan mencetak respon dalam log (komentar yang dinonaktifkan). Jika gagal, program akan mencetak kesalahan dalam log.
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d("TAG", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("TAG", response.errorBody()!!.string())
                }

                //Jika notifikasi berisi URL gambar, program akan mencoba mengambil gambar tersebut dengan getBitmapFromUrl. Jika gambar berhasil diambil,
                // program akan menampilkan notifikasi dengan gambar menggunakan showNotificationWithImage.
                if (!notification.data.imageUrl.isNullOrEmpty()) {
                    // Ambil gambar dari URL
                    val bitmap = getBitmapFromUrl(notification.data.imageUrl!!)

                    // Jika gambar berhasil diambil
                    if (bitmap != null) {
                        // Tampilkan notifikasi dengan gambar menggunakan BigPictureStyle
                        showNotificationWithImage(notification, bitmap)
                    }
                }
            }
            //Blok try-catch digunakan untuk menangkap dan menangani pengecualian yang mungkin
        // terjadi selama operasi pengiriman notifikasi dan pengambilan gambar.
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }

    // Fungsi untuk mendapatkan bitmap dari URL
    fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            Log.e("TAG", "Error fetching image from URL: ${e.message}")
            null
        }
    }

    fun showNotificationWithImage(notification: PushNotification, bitmap: Bitmap) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Konfigurasi notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notification.data.title)
            .setContentText(notification.data.message)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Tambahkan BigPictureStyle dengan gambar
        val style = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null as Bitmap?)
            .setBigContentTitle(notification.data.title)
            .setSummaryText(notification.data.message)

        builder.setStyle(style)

        // Tampilkan notifikasi
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }


    private fun showImagePreview(imagePath: String?) {
        // Periksa apakah path gambar tidak null atau kosong
        if (!imagePath.isNullOrEmpty()) {
            // Tampilkan tata letak gambar sebelum dikirim
            binding.layoutImagePreview.visibility = View.VISIBLE

            // Gunakan Glide atau Picasso untuk menampilkan gambar di ImageView
            // Contoh menggunakan Glide:
            Glide.with(this)
                .load(imagePath)
                .into(binding.imgPreview)

            // Tambahkan listener untuk tombol hapus gambar
            binding.btnRemoveImage.setOnClickListener {
                // Hapus path gambar yang dipilih
                selectedImagePath = null
                // Sembunyikan tata letak gambar sebelum dikirim
                binding.layoutImagePreview.visibility = View.GONE
            }
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        //Program menggunakan ResourcesCompat.getDrawable untuk mengambil drawable vektor berdasarkan ID yang diberikan. Jika drawable tidak ditemukan, program akan mencetak pesan kesalahan ke log
        // dan mengembalikan marker default dari BitmapDescriptorFactory.
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        //Program membuat bitmap dengan ukuran yang sama dengan drawable vektor menggunakan Bitmap.createBitmap. Konfigurasi bitmap menggunakan Bitmap.Config.ARGB_8888 untuk kualitas gambar
        // yang baik dengan saluran alfa (transparansi).
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        //Program membuat objek Canvas menggunakan bitmap yang baru dibuat, lalu mengatur batas drawable vektor agar sesuai dengan ukuran kanvas. DrawableCompat.setTint digunakan untuk mengubah warna drawable sesuai dengan parameter warna yang diberikan.
        // Terakhir, drawable vektor digambar pada kanvas.
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        //Bitmap yang dihasilkan kemudian diubah menjadi BitmapDescriptor menggunakan BitmapDescriptorFactory.fromBitmap
        // dan dikembalikan sebagai hasil fungsi.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //gMap = googleMap: Menyimpan instance GoogleMap yang disediakan oleh callback onMapReady.
        gMap = googleMap
        //gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)):
        // Mengatur gaya peta menggunakan file gaya yang disimpan di direktori raw.
        gMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.map_style
            )
        )
        //Mengaktifkan kontrol zoom.
        gMap.uiSettings.isZoomControlsEnabled = true
        //Mengaktifkan pemilih level dalam ruangan.
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        //Mengaktifkan kompas.
        gMap.uiSettings.isCompassEnabled = true
        //Mengaktifkan toolbar peta.
        gMap.uiSettings.isMapToolbarEnabled = true
        //Jika currentLatitude dan currentLongitude tidak null,
        // memanggil displayLocationOnMap untuk menampilkan lokasi saat ini di peta.

        if (currentLatitude != null && currentLongitude != null) {
            displayLocationOnMap(currentLatitude!!, currentLongitude!!)
        }
    }


    private fun displayLocationOnMap(latitude: Double, longitude: Double) {
        //Membuat objek LatLng dengan koordinat latitude dan longitude yang diberikan.
        val location = LatLng(latitude, longitude)
        //Menambahkan marker ke peta di lokasi yang ditentukan.
        gMap.addMarker(
            //Menetapkan posisi marker.
            MarkerOptions()
                .position(location)
                    //Mengatur ikon marker menggunakan fungsi vectorToBitmap untuk mengonversi drawable vektor
                // menjadi bitmap berwarna merah.
                .icon(vectorToBitmap(R.drawable.ic_baseline_location_on_24, Color.RED))
                    // Menetapkan judul marker.
                .title("Current Location")
        )
        //Menggerakkan kamera peta ke lokasi marker dengan zoom level 15.
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }


}