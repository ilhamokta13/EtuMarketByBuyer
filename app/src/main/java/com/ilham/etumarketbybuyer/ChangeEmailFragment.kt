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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.ilham.etumarketbybuyer.databinding.FragmentChangeEmailBinding
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeEmailFragment : Fragment() {
    lateinit var binding : FragmentChangeEmailBinding
    lateinit var auth : FirebaseAuth
    private lateinit var pref: SharedPreferences
    lateinit var userVm : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)
        //Mengatur SharedPreferences dan ViewModel.
        //Mendapatkan instance pengguna saat ini dari FirebaseAuth.
        //Mengatur visibilitas elemen UI berdasarkan keadaan.

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cvChangeEmailInputPass.visibility = View.VISIBLE
        binding.cvChangeEmail.visibility = View.GONE

        //Tombol "Next" (btnNext) digunakan untuk memverifikasi password pengguna sebelum mengizinkan perubahan email.
        //Jika password benar, UI akan menampilkan form untuk mengubah email.

        binding.btnNext.setOnClickListener {
            var pass = binding.edtChangeEmailPassword.text.toString()

            if (pass.isEmpty()) {
                binding.edtChangeEmailPassword.error = "Password Harus Terisi"
                binding.edtChangeEmailPassword.requestFocus()
                return@setOnClickListener
            }

            // cek password
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!,pass)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            binding.cvChangeEmailInputPass.visibility = View.GONE
                            binding.cvChangeEmail.visibility = View.VISIBLE
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            binding.edtChangeEmailPassword.error = "Password Salah"
                            binding.edtChangeEmailPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        //Tombol "Change Email" (btnChangeEmail) digunakan untuk mengubah alamat email pengguna.
        //Melakukan validasi email baru (tidak kosong dan sesuai format).
        //Jika validasi sukses, mengubah email melalui FirebaseAuth.
        //Mengupdate profil pengguna melalui ViewModel (updateuserprofile).

        binding.btnChangeEmail.setOnClickListener newEmail@{
            var newEmail = binding.edtChangeEmail.text.toString()

            updateuserprofile()

            if (newEmail.isEmpty()){
                binding.edtChangeEmail.error = "Email Harus Terisi"
                binding.edtChangeEmail.requestFocus()
                return@newEmail
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                binding.edtChangeEmail.error = "Email Tidak Valid"
                binding.edtChangeEmail.requestFocus()
                return@newEmail
            }

            user?.let {
                user.updateEmail(newEmail).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Email Berhasil Diubah", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_changeEmailFragment_to_profileFragment2)

                    } else {
                        Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }

    //Mengambil token dari SharedPreferences.
    //Memperbarui email di backend menggunakan ViewModel.
    //Menampilkan pesan toast jika pembaruan profil berhasil.
    fun updateuserprofile() {

        val token = pref.getString("token", "").toString()
        val email = binding.edtChangeEmail.text.toString()
        userVm.updateemail(token, email)

        userVm.responseupdateprofile.observe(viewLifecycleOwner){
            if (it != null) {
                Toast.makeText(context, "Update Profile Berhasil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    }

