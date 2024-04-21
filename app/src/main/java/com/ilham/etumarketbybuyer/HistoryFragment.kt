package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilham.etumarketbybuyer.databinding.FragmentHistoryBinding
import com.ilham.etumarketbybuyer.viewmodel.HistoryViewModel
import com.ilham.etumarketbybuyer.viewmodel.PaymentViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    lateinit var binding : FragmentHistoryBinding
    lateinit var pref: SharedPreferences
    lateinit var historyVm : HistoryViewModel
    lateinit var token : String

    private lateinit var handler: Handler
    private val interval: Long = 3600000 // 1 jam


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        historyVm =  ViewModelProvider(this).get(HistoryViewModel::class.java)

        token = pref.getString("token", "").toString()


        // Inisialisasi handler
        handler = Handler(Looper.getMainLooper())

        startRepeatingTask()



        if (pref.getString("token", "")!!.isEmpty()) {
            binding.rvListHistory.visibility = View.GONE
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Login")
                .setMessage("Anda Belum Login")
                .setCancelable(false)
                .setNegativeButton("Cancel") { dialog, which ->
                    // Respond to negative button press
                    findNavController().navigate(R.id.action_historyFragment2_to_homeFragment2)
                }
                .setPositiveButton("Login") { dialog, which ->
                    // Respond to positive button press
                    findNavController().navigate(R.id.action_historyFragment2_to_loginFragment)
                }
                .show()
        } else if (pref.getString("token", "")!!.isNotEmpty()) {

            getHistory(token)
        }
    }

    fun getHistory(token:String){
        historyVm.gethistory(token)
       historyVm.datahistory.observe(viewLifecycleOwner, Observer{
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            binding.rvListHistory.layoutManager = layoutManager
            if (it!= null) {
                binding.rvListHistory.adapter = HistoryAdapter(it)
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
}

