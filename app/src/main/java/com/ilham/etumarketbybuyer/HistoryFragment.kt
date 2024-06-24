package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import org.json.JSONObject

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    lateinit var binding : FragmentHistoryBinding
    lateinit var pref: SharedPreferences
    lateinit var historyVm : HistoryViewModel
    lateinit var pesananAdapter : HistoryAdapter
    lateinit var token : String




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

        pesananAdapter = HistoryAdapter(ArrayList())

        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        historyVm =  ViewModelProvider(this).get(HistoryViewModel::class.java)

        token = pref.getString("token", "").toString()





        getHistory(token)




        binding.etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pesananAdapter.filter(s.toString(), binding.statusFilterSpinner.selectedItem.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.rvListHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pesananAdapter
        }


        binding.statusFilterSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_array,
            android.R.layout.simple_spinner_item
        )

        binding.statusFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val status = parent.getItemAtPosition(position).toString()
                pesananAdapter.filter(binding.etSearchProduct.text.toString(), status)
//                pesananAdapter.filter(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        if (token.isEmpty() || isTokenExpired(token)) {
            binding.rvListHistory.visibility = View.GONE
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Login")
                .setMessage("Your session has expired. Please login again.")
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
        } else {
            getHistory(token)
        }





    }

    fun getHistory(token:String){
        historyVm.gethistory(token)
        historyVm.datahistory.observe(viewLifecycleOwner, Observer{

            pesananAdapter.notifyDataSetChanged()
           pesananAdapter.filteredList = it
           pesananAdapter.listhistory = it
            pesananAdapter.setAllHistory(it)

            it.forEach { history->
               pesananAdapter.filter("", history.status)
            }

//            pesananAdapter.filter("", it)

            // Separate items by color before updating the UI
//           pesananAdapter.separateItemsByColor()
           // Notify adapter about the changes
           pesananAdapter.notifyDataSetChanged()


           val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
           binding.rvListHistory.layoutManager = layoutManager
           binding.rvListHistory.adapter = pesananAdapter


        })
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
}

