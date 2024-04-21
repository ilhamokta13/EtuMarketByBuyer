package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ilham.etumarketbybuyer.databinding.FragmentLoginBinding
import com.ilham.etumarketbybuyer.model.login.LoginBody
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    lateinit var pref: SharedPreferences
    private lateinit var userVm : UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnMasukLogin.setOnClickListener {
            if (binding.etEmailLogin.text.toString().isEmpty()) {
                binding.etEmailLogin.setError("Isi Username")
            } else if (binding.etPasswordLogin.text.toString().isEmpty()) {
                binding.etPasswordLogin.setError("Isi Password")
            } else if (binding.etPasswordLogin.text.toString().length < 6) {
                Toast.makeText(context, "Password kurang dari 6 karakter", Toast.LENGTH_SHORT).show()
            }
            else {
                login()




            }
        }





        binding.lupaPassword.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_changePassFragment)

        }

        binding.tvGoToRegister.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Tidak melakukan apa-apa ketika tombol kembali ditekan
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


    }

    fun login() {
        val email = binding.etEmailLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase authentication successful
                    binding.etEmailLogin.setText("")
                    binding.etPasswordLogin.setText("")

                    // Now, check your custom authentication logic
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        userVm.responselogin.observe(viewLifecycleOwner, Observer { response ->
                            if (response.message == "Success") {
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment2)
                                Toast.makeText(context, "User Berhasil Login", Toast.LENGTH_SHORT).show()

                                // Save token to SharedPreferences
                                val sharedPref = pref.edit()
                                sharedPref.putString("token", response.token)
                                sharedPref.apply()
                            } else {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                        })

                        userVm.postlogin(LoginBody(email, password))
                    } else {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Firebase authentication failed
                    Toast.makeText(context, "Firebase Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }




    }




}