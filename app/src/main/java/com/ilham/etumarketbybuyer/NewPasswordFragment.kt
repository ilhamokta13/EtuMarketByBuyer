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
import androidx.navigation.fragment.findNavController
import com.ilham.etumarketbybuyer.databinding.FragmentNewPasswordBinding
import com.ilham.etumarketbybuyer.model.changepass.PostNewPassword
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPasswordFragment : Fragment() {
    lateinit var binding : FragmentNewPasswordBinding
    lateinit var pref : SharedPreferences
    lateinit var userVm : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnSave.setOnClickListener {
            val inputtoken = binding.inputToken.text.toString()
            val inputpassbaru = binding.inputPassbaru.text.toString()

            if (inputtoken.isNotEmpty() && inputpassbaru.isNotEmpty()) {
                val postNewPassword = PostNewPassword(inputpassbaru, inputtoken)

                userVm.newpassword(postNewPassword)

                userVm.responsenewpass.observe(viewLifecycleOwner) { response ->
                    if (response.message == "Password has been reset successfully") {
                        Toast.makeText(context, "Ganti Password Telah Berhasil", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_newPasswordFragment_to_loginFragment)
                    } else {
                        Toast.makeText(context, "Token tidak sesuai. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Token dan password baru tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            }
        }


    }


}