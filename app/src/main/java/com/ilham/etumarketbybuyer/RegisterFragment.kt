package com.ilham.etumarketbybuyer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ilham.etumarketbybuyer.databinding.FragmentRegisterBinding
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    lateinit var binding : FragmentRegisterBinding
    lateinit var pref : SharedPreferences
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    lateinit var userVm : UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)



        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.tvMasukdisini.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnDaftarRegister.setOnClickListener {
            val username = binding.etNamaRegister.text.toString()
            val email = binding.etEmailRegister.text.toString()
            val pass = binding.etEmailPasswordRegister.text.toString()
            val role = binding.etRole.text.toString()
            val telepon = binding.etPhoneRegister.text.toString()


            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()  || telepon.isEmpty() || role.isEmpty())  {
                Toast.makeText(requireContext(), "Please fill all the field", Toast.LENGTH_SHORT).show()

            } else {
                userVm.postregist(username, email, pass, telepon, role,)
                register(username,email,pass)
                userVm.responseregister.observe(viewLifecycleOwner){
                    if (it.message == "Register success") {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()

                        val sharedPref = pref.edit()
                        sharedPref.putString("email", email)
                        sharedPref.putString("telephone", telepon)
                        sharedPref.putString("fullname", username)
                        sharedPref.putString("password", pass)
                        //sharedPref.putString("idbuyer", it.data!!._id)
                        sharedPref.apply()

                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        Toast.makeText(context, "Berhasil Registrasi", Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        Toast.makeText(context, "Regis tidak berhasil", Toast.LENGTH_SHORT).show()

                    }

                }

            }
        }


    }

    private fun register(name: String, user: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val user: FirebaseUser? = firebaseAuth.currentUser
                val userId: String = user!!.uid

                databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("userId", userId)
                hashMap.put("fullname", name)
                hashMap.put("profileImage", "")

                databaseReference.setValue(hashMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //open home activity
                        binding.etNamaRegister.setText(" ")
                        binding.etEmailRegister.setText("")
                        binding.etEmailPasswordRegister.setText("")

//                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                    }
                }
            }
        }
    }





}


