package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.ilham.etumarketbybuyer.databinding.FragmentResetPassBinding


class ResetPassFragment : Fragment() {

    lateinit var binding : FragmentResetPassBinding
    lateinit var pref : SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResetPassBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref =requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        binding.btnReset.setOnClickListener {
            val email = binding.edtEmailReset.text.toString()
            val edtEmail= binding.edtEmailReset

            // jika email kosong
            if (email.isEmpty()){
                edtEmail.error= "Email Tidak Boleh Kosong"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            // Jika email tidak valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtEmail.error= "Email Tidak Valid"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(context, "Email Reset Password Telah Dikirim", Toast.LENGTH_SHORT).show()

                    Navigation.findNavController(binding.root).navigate(R.id.action_resetPassFragment_to_changePassFragment)

                } else {
                    Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}


