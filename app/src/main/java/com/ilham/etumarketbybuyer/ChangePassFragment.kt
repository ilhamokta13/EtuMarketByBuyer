package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ilham.etumarketbybuyer.databinding.FragmentChangePassBinding
import com.ilham.etumarketbybuyer.model.changepass.DataChangePass
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePassFragment : Fragment() {

    lateinit var binding : FragmentChangePassBinding
    lateinit var pref : SharedPreferences
    lateinit var userVm : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePassBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref= requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        changepassword()


    }

    fun changepassword() {

        val token = pref.getString("token", "").toString()
        val inputpasslama = binding.inputPasslama.text.toString()
        val inputpassbaru = binding.inputPassbaru.text.toString()
        val dataUser = DataChangePass(inputpasslama,inputpassbaru)
        userVm.changepass(token,dataUser)
        userVm.responsechangepass.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, "Update Berhasil", Toast.LENGTH_SHORT)
            }
        }


    }


}