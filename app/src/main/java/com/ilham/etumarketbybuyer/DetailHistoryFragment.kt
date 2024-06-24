package com.ilham.etumarketbybuyer

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ilham.etumarketbybuyer.databinding.FragmentDetailHistoryBinding
import com.ilham.etumarketbybuyer.model.status.PostUpdateStatus
import com.ilham.etumarketbybuyer.viewmodel.HistoryViewModel
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class DetailHistoryFragment : Fragment() {
    lateinit var binding: FragmentDetailHistoryBinding
    lateinit var pref: SharedPreferences
    lateinit var historyVm: HistoryViewModel
    lateinit var userVm: UserViewModel
    lateinit var token: String
    private var selectedImageUri: Uri? = null

    private var imageMultipart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null

//    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            selectedImageUri = result.data?.data
//            binding.uploadImage.setImageURI(selectedImageUri)
//        }
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        historyVm = ViewModelProvider(this).get(HistoryViewModel::class.java)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)
        token = pref.getString("token", "").toString()

        val kodetransaction = arguments?.getString("transcode")
        val IdProduct = arguments?.getString("productsid")

        observeDetailProduct(kodetransaction!!, IdProduct!!)
        historyVm.gethistory(token)

        if (kodetransaction != null && IdProduct != null) {
            observeDetailProduct(kodetransaction, IdProduct)
        } else {
            Toast.makeText(context, "Transcode or ProductID is null", Toast.LENGTH_SHORT).show()
        }


        binding.uploadImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            pickImageLauncher.launch(intent)

            getContent.launch("image/*")
        }

        //Mengambil status pesanan yang dipilih dari RadioGroup (rgStatusPesanan).
        // Pilihan status yang tersedia adalah "Selesai", "Dibatalkan", dan "CashOnDelivery".
        binding.btnUpdateStatus.setOnClickListener {
            val selectedStatus = when (binding.rgStatusPesanan.checkedRadioButtonId) {
                R.id.terimapesanan -> "Selesai"
                R.id.Dibatalkan -> "Dibatalkan"
                R.id.Cod -> "CashOnDelivery"
                else -> null

            }

            //Jika tidak ada status yang dipilih, akan menampilkan pesan Toast "Status harus dipilih".

            if (selectedStatus == null) {
                Toast.makeText(requireContext(), "Status harus dipilih", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Jika tidak ada status yang dipilih, akan menampilkan pesan Toast "Status harus dipilih".

            if (imageMultipart == null) {
                Toast.makeText(requireContext(), "Harap sertakan foto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            //Mengonversi kode transaksi menjadi RequestBody.
                val kodeTransaksi = kodetransaction!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val productID = IdProduct!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val status = selectedStatus.toRequestBody("text/plain".toMediaTypeOrNull())

            //Memanggil fungsi updateStatus dari historyVm (ViewModel) untuk memperbarui status pesanan dengan
            // mengirim data yang sudah dipersiapkan.

                historyVm.updateStatus(token, kodeTransaksi, imageMultipart!!, productID, status)

            //Mengamati perubahan data profil pengguna.
            // Jika ada perubahan, teks pada binding.welcome akan diperbarui dengan nama lengkap pengguna.
            userVm.dataprofile.observe(viewLifecycleOwner) {
                val fullname = it.fullName
                binding.welcome.text = "Welcome , $fullname"
            }



            //Jika pesan dari hasil pembaruan adalah "Berhasil update status transaksi", akan menampilkan pesan Toast "Berhasil Update Status".
            //Jika tidak, akan menampilkan pesan Toast "Tidak Berhasil Update Status".
            historyVm.dataupdatestatus.observe(viewLifecycleOwner) {
                if (it.message == "Berhasil update status transaksi") {
                    Toast.makeText(context, "Berhasil Update Status", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Tidak Berhasil Update Status", Toast.LENGTH_LONG)
                        .show()
                }
            }






        }
    }



        fun observeDetailProduct(kodetransaction: String, productId: String) {
            historyVm.datahistory.observe(viewLifecycleOwner) { listDataPesanan ->
                listDataPesanan.forEach { dataPesanan ->
                    // Memeriksa apakah kode transaksi cocok
                    if (dataPesanan.kodeTransaksi == kodetransaction) {
                        dataPesanan.products.forEach { product ->
                            // Memeriksa apakah productID cocok
                            if (product.productID.id == productId) {
                                binding.tvNamaproductdetail.text =
                                    "Nama Produk : ${product.productID.nameProduct}"
                                binding.tvCategory.text = "Kategori : ${product.productID.category}"
                                binding.tvQuantity.text = "Jumlah Barang : ${product.quantity}"
                                binding.tvHarga.text = "Harga : ${product.productID.price}"

                                val quantity = product.quantity
                                val harga = product.productID.price
                                val totalharga = quantity * harga
                                binding.tvTotalharga.text = "Total Harga : ${totalharga}"

                                Glide.with(requireContext())
                                    .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${product.productID.image}")
                                    .into(binding.ivProductimagedetail)
                            }
                        }
                    }
                }
            }
        }


//    private fun getPathFromUri(uri: Uri): String {
//        var path = ""
//        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
//        if (cursor != null) {
//            cursor.moveToFirst()
//            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            if (index != -1) {
//                path = cursor.getString(index)
//            }
//            cursor.close()
//        }
//        return path
//    }


    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val contentResolver: ContentResolver = requireContext().contentResolver
                val type = contentResolver.getType(it)
                imageUri = it

                val fileNameimg = "${System.currentTimeMillis()}.png"
                binding.uploadImage.setImageURI(it)
                Toast.makeText(context, "$imageUri", Toast.LENGTH_SHORT).show()

                val tempFile = File.createTempFile("and1-", fileNameimg, null)
                imageFile = tempFile
                val inputstream = contentResolver.openInputStream(uri)
                tempFile.outputStream().use { result ->
                    inputstream?.copyTo(result)
                }
                val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                imageMultipart =
                    MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
            }


        }






}




