package com.ilham.etumarketbybuyer

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.util.Base64Utils.decode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ilham.etumarketbybuyer.databinding.FragmentProfileBinding
import com.ilham.etumarketbybuyer.model.chat.User
import com.ilham.etumarketbybuyer.model.profile.DataProfile
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import android.util.Base64
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var pref: SharedPreferences
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    lateinit var userVm : UserViewModel
    lateinit var token : String
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2020





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)
        token = pref.getString("token", "").toString()



        if (token.isEmpty() || isTokenExpired(token)) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Login")
                .setMessage("Your session has expired. Please login again.")
                .setCancelable(false)
                .setNegativeButton("Cancel") { dialog, which ->
                    // Tetap di halaman profil
                }
                .setPositiveButton("Login") { dialog, which ->
                    // Navigasi ke halaman login
                    findNavController().navigate(R.id.action_profileFragment2_to_loginFragment)
                }
                .show()
        } else {
            getprofile()
        }





        binding.btnUpdate.setOnClickListener {
            updateuserprofile()

        }

        binding.btnChangeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment2_to_changeEmailFragment)
        }

        binding.btnLogout.setOnClickListener {
            val editor = pref.edit()
            pref.edit().clear().apply()
            editor.apply()
            Log.d("DataToken", pref.getString("token", "").toString())
            Toast.makeText(activity, "user berhasil logout", Toast.LENGTH_LONG).show()
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_profileFragment2_to_loginFragment)
        }

    }










    fun updateuserprofile() {
        //Mengambil Token dan Data Input
        val token = pref.getString("token", "").toString()
        val inputnama = binding.txtFullname.text.toString()
        val inputtelepon = binding.txtTelephone.text.toString()
        val inputrole = binding.txtRole.text.toString()
        val inputshopname = binding.txtShopname.text.toString()

        //Memanggil Fungsi updateprofile dari ViewModel:
        userVm.updateprofile(token,inputnama,inputtelepon,inputrole, inputshopname)

        //Mengamati Hasil Pembaruan Profil:
        userVm.responseupdateprofile.observe(viewLifecycleOwner){
            if (it != null) {
                Toast.makeText(context, "Update Profile Berhasil", Toast.LENGTH_SHORT).show()
            }
        }



    }


    fun getprofile(){

        //Mengambil Token
        val token = pref.getString("token", "").toString()
        //Memanggil Fungsi getUserProfile dari ViewModel:
        userVm.getUserProfile(token)

        //Mengamati Data Profil Pengguna
        userVm.dataprofile.observe(viewLifecycleOwner){
            binding.txtFullname.setText(it.fullName)
            binding.txtTelephone.setText(it.telp)
            binding.txtShopname.setText(it.shopName)
            binding.txtRole.setText(it.role)
        }
    }


    private fun isTokenExpired(token: String): Boolean {
        try {
            //Memisahkan dan Mendekode Token
            val split = token.split(".")
            val decodedBytes = Base64.decode(split[1], Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(decodedString)
            //Memeriksa Waktu Kedaluwarsa Token
            val exp = jsonObject.getLong("exp")
            val currentTime = System.currentTimeMillis() / 1000
            return currentTime > exp
            //Menangani Kesalahan
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true // Jika terjadi kesalahan, anggap token sudah kedaluwarsa
    }





















}
