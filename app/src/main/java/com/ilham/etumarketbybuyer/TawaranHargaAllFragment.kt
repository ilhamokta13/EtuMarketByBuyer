package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbybuyer.databinding.FragmentTawaranHargaAllBinding
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TawaranHargaAllFragment : Fragment() {
   lateinit var binding : FragmentTawaranHargaAllBinding
    lateinit var pref: SharedPreferences
    lateinit var productVm: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTawaranHargaAllBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        // Check login status
        val token = pref.getString("token", "").toString()

        productVm.gettawarharga(token)
        productVm.datagettawar.observe(viewLifecycleOwner){
            binding.rvListPenawaran.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false
            )

            if (it!= null) {
                binding.rvListPenawaran.adapter = TawarHargaAdapter(it)
            }

        }
    }


}