package com.ilham.etumarketbybuyer

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.ilham.etumarketbybuyer.databinding.FragmentPengirimanBinding
import com.ilham.etumarketbybuyer.pengiriman.PostPengiriman
import com.ilham.etumarketbybuyer.viewmodel.HistoryViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class PengirimanFragment : Fragment() {
    lateinit var binding : FragmentPengirimanBinding
    lateinit var historyVm: HistoryViewModel
    lateinit var productVm : ProductViewModel
    lateinit var pref: SharedPreferences
    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPengirimanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        historyVm = ViewModelProvider(this).get(HistoryViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        token = pref.getString("token", "").toString()
        val kodetransaction = arguments?.getString("kodetransac")

        binding.findlocation.setOnClickListener {
            getLocation()
        }

        binding.saveButton.setOnClickListener {

            val latitude = binding.latitude.text.toString()
            val longitude = binding.longitude.text.toString()


            val postpengiriman = PostPengiriman(
                latitude.toString().toDouble(),
                longitude.toString().toDouble(),
                kodetransaction!!
            )
            historyVm.kirimlokasi(token, postpengiriman)

            historyVm.datalocpengiriman.observe(viewLifecycleOwner){
               if (it.message == "Biaya pengiriman berhasil dihitung") {
                   Toast.makeText(context, "Lokasi Berhasil dikirim", Toast.LENGTH_SHORT).show()
               } else{
                   Toast.makeText(context, "Lokasi Gagal dikirim", Toast.LENGTH_SHORT).show()
                   }
            }
        }

    }

    private fun getLocation() {
        val fusedLocProvClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 10
            )
        } else {
            fusedLocProvClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    binding.longitude.text = location.latitude.toString()
                    binding.latitude.text = location.longitude.toString()
                    binding.altitude.text = location.altitude.toString()
                    binding.edAcc.text = "${location.accuracy}%"

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            binding.uploadlocation.text = locName
                            productVm.saveLokasi(locName)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}