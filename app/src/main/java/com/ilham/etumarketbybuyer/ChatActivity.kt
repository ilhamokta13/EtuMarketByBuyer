package com.ilham.etumarketbybuyer

import android.Manifest
import android.app.NotificationManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.ilham.etumarketbybuyer.databinding.ActivityChatBinding
import com.ilham.etumarketbybuyer.model.chat.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URL

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    var topic = ""
    private lateinit var pref: SharedPreferences
    private var selectedImagePath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var audioFileName: String = ""



    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val NOTIFICATION_ID = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("Berhasil", Context.MODE_PRIVATE)


//        if (pref.getString("token", "")!!.isEmpty()) {
//            MaterialAlertDialogBuilder(this)
//                .setTitle("Login")
//                .setMessage("Anda Belum Login")
//                .setCancelable(false)
//                .setNegativeButton("Cancel") { dialog, which ->
//                    // Respond to negative button press
//                    val intent = Intent(this, ChatActivity::class.java)
//                    this.startActivity(intent)
//                }
//                .setPositiveButton("Login") { dialog, which ->
//                    val loginFragment = LoginFragment()
//
//                    // Start a fragment transaction
//                    val transaction = supportFragmentManager.beginTransaction()
//
//                    // Replace the current fragment with the login fragment
//                    transaction.replace(R.id.loginFragment, loginFragment)
//
//                    // Commit the transaction
//                    transaction.commit()
//                }
//                .show()
//        } else if (pref.getString("token", "", )!!.isNotEmpty()) {
//
//
//
//        }

        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)





        FirebaseApp.initializeApp(this)


        var intent = getIntent()
        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("fullname")


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
            // Extracted the user ID and user name from the intent
            val userId = intent.getStringExtra("userId")
            val userName = intent.getStringExtra("fullname")

            // Get the message from the EditText
            val message: String = binding.etMessage.text.toString().trim()

            // Check if an image is selected
            if (!selectedImagePath.isNullOrEmpty()) {
                // Image selected, upload to Firebase Storage
                uploadImage(message)
            } else if (message.isNotEmpty()) {
                // Only send the text message if there is text and no image is selected
                sendMessage(firebaseUser!!.uid, userId!!, message)
                binding.etMessage.setText("")  // Clear the EditText
            }

            // Reset selectedImagePath
            selectedImagePath = null

            topic = "/topics/$userId"

            // If there's a message or an image URL, send a notification
            if (!message.isNullOrEmpty() || !selectedImagePath.isNullOrEmpty()) {
                PushNotification(
                    NotificationData(userName!!, message),
                    topic
                ).also {
                    sendNotification(it)
                }
            }

            // Sembunyikan tata letak gambar sebelum dikirim
            binding.layoutImagePreview.visibility = View.GONE
        }

        readMessage(firebaseUser!!.uid, userId)

    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImagePath = data.data?.toString()
            // Tampilkan gambar sebelum dikirim
            showImagePreview(selectedImagePath)
        }
    }




    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    // Add this constant
    private val IMAGE_PICK_REQUEST = 101


    private fun uploadImage(message: String) {
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


    private fun sendMessage(senderId: String, receiverId: String, message: String, imageUrl: String? = null) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        val chat = Chat(senderId, receiverId, message, imageUrl)

        reference!!.child("Chat").push().setValue(chat)

    }

    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                binding.chatRecyclerView.adapter = chatAdapter
            }
        })
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

                // Jika notifikasi berisi gambar
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














}