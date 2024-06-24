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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ilham.etumarketbybuyer.databinding.FragmentRegisterBinding
import com.ilham.etumarketbybuyer.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    lateinit var pref: SharedPreferences
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    lateinit var userVm: UserViewModel
    private var titleAd: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)


        val hintTitle = resources.getStringArray(R.array.Role)

        formTitle(hintTitle)





        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registerFragment_to_loginFragment)
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
            val shopName = binding.etShopname.text.toString()


            if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || telepon.isEmpty() || role.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all the field", Toast.LENGTH_SHORT)
                    .show()

            } else {
                checkIfUsernameExists(username, email, pass, telepon, role, shopName)
//                userVm.postregist(username, email, pass, telepon, role, shopName)
//                register(shopName, email, pass)
//                userVm.responseregister.observe(viewLifecycleOwner) {
//                    if (it.message == "Register success") {
//                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
//
//                        val sharedPref = pref.edit()
//                        sharedPref.putString("email", email)
//                        sharedPref.putString("telephone", telepon)
//                        sharedPref.putString("fullname", username)
//                        sharedPref.putString("password", pass)
//                        //sharedPref.putString("idbuyer", it.data!!._id)
//                        sharedPref.apply()
//
//                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//                        Toast.makeText(context, "Berhasil Registrasi", Toast.LENGTH_SHORT)
//                            .show()
//                    } else {
//                        Toast.makeText(context, "Regis tidak berhasil", Toast.LENGTH_SHORT).show()
//
//                    }
//
//                }

            }
        }


    }

    //Fungsi ini menerima tiga parameter: name (nama lengkap pengguna), user (email pengguna), dan pass (kata sandi pengguna)


    private fun checkIfUsernameExists(username: String, email: String, pass: String, telepon: String, role: String, shopName: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByChild("fullname").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(requireContext(), "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show()
                } else {
                    userVm.postregist(username, email, pass, telepon, role, shopName)
                    register(shopName, email, pass)
                    userVm.responseregister.observe(viewLifecycleOwner) {
                        if (it.message == "Register success") {
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                            val sharedPref = pref.edit()
                            sharedPref.putString("email", email)
                            sharedPref.putString("telephone", telepon)
                            sharedPref.putString("fullname", username)
                            sharedPref.putString("password", pass)
                            sharedPref.apply()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            Toast.makeText(context, "Berhasil Registrasi", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Regis tidak berhasil", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun register(name: String, user: String, pass: String) {
        //Membuat pengguna baru dengan email dan kata sandi
        firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                //variabel user menyimpan pengguna yang saat ini sedang login, dan userId menyimpan UID (User ID) dari pengguna tersebut
                val user: FirebaseUser? = firebaseAuth.currentUser
                val userId: String = user!!.uid
                //menginisialisasi databaseReference ke lokasi di Firebase Realtime Database di mana data pengguna baru akan disimpan, yaitu di bawah node "Users" dengan userId sebagai child
                databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(userId)


                //Membuat HashMap untuk menyimpan data pengguna
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("userId", userId)
                hashMap.put("fullname", name)
                hashMap.put("profileImage", "")
                //setValue digunakan untuk menyimpan HashMap ke database di lokasi yang ditentukan oleh databaseReference. Jika penyimpanan data berhasil,
                // beberapa elemen UI dibersihkan, seperti mengosongkan teks dari beberapa EditText.
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

   // Fungsi ini bernama formTitle dan menerima satu parameter yaitu hintTitle, yang merupakan sebuah array string

    private fun formTitle(hintTitle: Array<String>) {
        binding.etRole.apply {
            //Membuat sebuah ArrayAdapter bernama adapterTitle yang akan mengubah array hintTitle menjadi item-item yang bisa ditampilkan dalam dropdown list.
            // R.layout.dropdown_item adalah layout yang digunakan untuk menampilkan setiap item dalam dropdown list.
            val adapterTitle = ArrayAdapter(requireContext(), R.layout.dropdown_item, hintTitle)
            //Mengatur adapterTitle sebagai adapter untuk etRole,
            // sehingga dropdown list akan diisi dengan item-item dari hintTitle
            setAdapter(adapterTitle)
            //Mengatur Hint
            hint = "Title"
            //Menetapkan onItemClickListener untuk menangani kejadian ketika sebuah item dalam dropdown list dipilih
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                titleAd = adapterTitle.getItem(position).toString()
            }
        }
    }

    private fun isSequentialNumber(number: String): Boolean {
        val sequentialPattern = "0123456789"
        return number.contains(sequentialPattern)



    }
}


