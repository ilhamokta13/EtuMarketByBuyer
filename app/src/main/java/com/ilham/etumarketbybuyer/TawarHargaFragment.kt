package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilham.etumarketbybuyer.databinding.FragmentTawarHargaBinding
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TawarHargaFragment : Fragment() {
    lateinit var binding : FragmentTawarHargaBinding
    lateinit var pref:SharedPreferences
    lateinit var productVm: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTawarHargaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        // Check login status
        val token = pref.getString("token", "").toString()

        binding.btnLihathasiltawar.setOnClickListener {
            findNavController().navigate(R.id.action_tawarHargaFragment_to_tawaranHargaAllFragment)
        }




        //Mengecek status login pengguna dengan memeriksa token yang disimpan di SharedPreferences.
        if (token.isEmpty()) {
            //Jika pengguna belum login, menampilkan dialog untuk login.
            showLoginDialog()
        } else {
            // Check if the user has visited the chat page
            val hasVisitedChat = pref.getBoolean("hasVisitedChat", false)
            //Jika belum, menampilkan dialog untuk mengunjungi halaman chat.
            if (!hasVisitedChat) {
                showChatDialog()
            } else {
                //Jika sudah, mengatur listener untuk tombol reset.
                binding.btnReset.setOnClickListener {
                    posttawarharga()
                }
            }
        }
    }
    //Menampilkan dialog yang meminta pengguna untuk login atau
    // mengunjungi halaman chat jika syarat tersebut belum terpenuh
    private fun showLoginDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Login Required")
            .setMessage("You need to login to access this feature.")
            .setPositiveButton("Login") { dialog, which ->
                findNavController().navigate(R.id.action_tawarHargaFragment_to_loginFragment)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                findNavController().navigate(R.id.action_tawarHargaFragment_to_homeFragment2)
            }
            .show()
    }

    private fun showChatDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chat Required")
            .setMessage("You need to visit the chat page before you can make an offer.")
            .setPositiveButton("Go to Chat") { dialog, which ->
                findNavController().navigate(R.id.action_tawarHargaFragment_to_userFragment)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                findNavController().navigate(R.id.action_tawarHargaFragment_to_homeFragment2)
            }
            .show()
    }




    fun posttawarharga() {
        //Mengambil token dari SharedPreferences.
        val token = pref.getString("token", "").toString()
        //Mengambil data tawaran dari argumen fragment.
        val getData = arguments?.getString("tawar")
        //Memvalidasi input harga dari pengguna.
        val tawarHargaString = binding.etHargaTawar.text.toString()

            // Validasi input kosong
            if (tawarHargaString.isEmpty()) {
                Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return
            }

            val tawarHarga = try {
                tawarHargaString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return
            }
        // Kirim data harga ke ViewModel
        //Jika validasi berhasil, mengirim data harga ke ViewModel untuk diproses.
        productVm.posttawar(token, getData.toString(), tawarHarga.toInt())

       //Mengamati hasil dari ViewModel dan menampilkan pesan toast sesuai dengan hasilnya.
        productVm.dataposttawar.observe(viewLifecycleOwner) {
            if (it.message == "Offer made successfully") {
//                findNavController().navigate(R.id.action_tawarHargaFragment_to_homeFragment2)
                Toast.makeText(context, "Harga Berhasil Diupdate", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("Create Seller", "Error: ${it.message}")
                Toast.makeText(context, "Failed to update price", Toast.LENGTH_SHORT).show()
            }
        }
    }
}