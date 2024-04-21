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
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.databinding.FragmentDetailHistoryBinding
import com.ilham.etumarketbybuyer.model.status.PostUpdateStatus
import com.ilham.etumarketbybuyer.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailHistoryFragment : Fragment() {
    lateinit var binding : FragmentDetailHistoryBinding
    lateinit var pref : SharedPreferences
    lateinit var historyVm : HistoryViewModel
    lateinit var token : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        historyVm = ViewModelProvider(this).get(HistoryViewModel::class.java)
        token = pref.getString("token", "").toString()

        val kodetransaction = arguments?.getString("transcode")
        val IdProduct = arguments?.getString("productsid")


        binding.btnUpdateStatus.setOnClickListener {
            val selectedStatus = when (binding.rgStatusPesanan.checkedRadioButtonId) {
                R.id.pending -> "Pending"
                R.id.paid -> "Paid"
                R.id.Ondelivery -> "On Delivery"
                R.id.Delivered -> "Delivered"
                R.id.Expired -> "Expired"
                R.id.Failed -> "Failed"
                else -> {
                    // If nothing is selected, show an error message and return
                    Toast.makeText(requireContext(), "Please select a status", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }

            val postupdate = PostUpdateStatus(kodetransaction!!,IdProduct!!, selectedStatus)

            historyVm.updateStatus(token, postupdate)
        }

        historyVm.dataupdatestatus.observe(viewLifecycleOwner){
            if (it.message == "Berhasil update status transaksi"){
                Toast.makeText(context, "Berhasil Update Status", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "Tidak Berhasil Update Status", Toast.LENGTH_LONG).show()
            }
        }

        historyVm.gethistory(token)
        observeDetailProduct(kodetransaction,IdProduct)



    }

    private fun observeDetailProduct(kodetransaction: String?, productId: String?) {
        historyVm.datahistory.observe(viewLifecycleOwner) { listDataPesanan ->
            listDataPesanan.forEach { dataPesanan ->

                dataPesanan.products.forEach { products->
                    if (products.productID.id == productId) {
                        binding.tvTotalharga.text = dataPesanan.total.toString()

                        dataPesanan.products.forEach { product ->

                            binding.tvNamaproductdetail.text = product.productID.nameProduct
                            binding.tvCategory.text = product.productID.category
                            binding.tvQuantity.text = product.quantity.toString()
                            binding.tvHarga.text = product.productID.price.toString()

                            Glide.with(requireContext())
                                .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${product.productID.image}")
                                .into(binding.ivProductimagedetail)


                        }



                    }
                }


            }
        }





    }


}