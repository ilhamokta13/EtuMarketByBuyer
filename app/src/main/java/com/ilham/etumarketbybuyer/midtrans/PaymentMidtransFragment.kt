package com.ilham.etumarketbybuyer.midtrans

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.ilham.etumarketbybuyer.databinding.FragmentPaymentMidtransBinding
import com.ilham.etumarketbybuyer.model.transaksi.MidtransResponse
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.PaymentViewModel

import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMidtransFragment : Fragment() {
    lateinit var binding: FragmentPaymentMidtransBinding
    private lateinit var idProductCart: String
    private lateinit var pref: SharedPreferences
    lateinit var paymentVm: PaymentViewModel
    lateinit var midtransResponse: MidtransResponse
    private lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var cartVm: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentMidtransBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)
        cartVm = ViewModelProvider(this).get(CartViewModel::class.java)

        val serverToken = "03cdd1b2-ad06-46f5-aad8-047a6e7c2755"
        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result?.resultCode == RESULT_OK) {
                    result.data?.let {
                        val transactionResult =
                            it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                        Toast.makeText(
                            context,
                            "${transactionResult?.transactionId}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }




        SdkUIFlowBuilder.init()
            .setClientKey("Mid-client-MwQBPUpVbPpDzRbT")
            .setContext(context)
            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->

            })


            .setMerchantBaseUrl("https://7895jr9m-3000.asse.devtunnels.ms/")


            .enableLog(true)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .setLanguage("id")
            .buildSDK();

//        paymentVm.midtransResponse.observe(viewLifecycleOwner) {
//            val tokenmidtrans = it.token
//            Log.d("PaymentMidtrans", "Token: $tokenmidtrans")
//            UiKitApi.getDefaultInstance().startPaymentUiFlow(
//                requireActivity(), // Activity
//                launcher,
//                tokenmidtrans// ActivityResultLauncher
//                // Snap Token
//            )
//        }


//        binding.btnbayar.setOnClickListener {
//            val getData =  arguments?.getParcelable<DataAllProduct>("detail") as DataAllProduct
//            idProductCart = getData.id
//
//            val namapengguna = getData.productID.
//            val email = getData.productID.sellerID.email
//            val phone = getData.productID.sellerID.telp
//
//        val belanja = jmlcart
//
//        val harga = getData.productID.price
//
//            val harga = getData.productID.price
//
//            val keseluruhanharga = jmlcart * harga.toDouble()
//
//
//            binding.tvJumlahCart.text = jmlcart.toString()
//
//
//            val dataCart = PostTransaction(listOf(idProductCart), listOf(jmlcart), keseluruhanharga.toInt())
//            val token = pref.getString("token", "").toString()
//
//            binding.tvTotalHarga.text = keseluruhanharga.toString()
//
//            paymentVm.postpayment(token, dataCart)
//
//
//
//            val transactionRequest = TransactionRequest(
//                "ETU-Market-" + System.currentTimeMillis().toShort() + "",
//                keseluruhanharga
//            )
//
//            val detail = ItemDetails(
//                idProductCart,
//                harga.toDouble(),
//                jmlcart.toInt(),
//                namaproduk
//            )
//            val itemDetails = ArrayList<ItemDetails>()
//
//            itemDetails.add(detail)
//
//
//
//            uiKitDetails(transactionRequest,"Ilham", "0856373829", "iljhamok@gmail.com")
//
//            transactionRequest.itemDetails = itemDetails
//
//            MidtransSDK.getInstance().transactionRequest = transactionRequest
//            MidtransSDK.getInstance().startPaymentUiFlow(context)
//
//
//            val tokenmidtrans = paymentVm.midtransResponse.value?.token
//            if (tokenmidtrans != null) {
//                // Lakukan sesuatu dengan token, misalnya menyimpan atau menggunakan untuk keperluan lain
//                Log.d("PaymentMidtrans", "Token: $tokenmidtrans")
//
//                UiKitApi.getDefaultInstance().startPaymentUiFlow(
//                    requireActivity(), // Activity
//                    launcher,
//                    tokenmidtrans// ActivityResultLauncher
//                    // Snap Token
//                )
//
//            } else {
//                Log.e("PaymentMidtrans", "Token is null")
//                // Tambahkan pesan kesalahan atau tindakan yang sesuai di sini
//                Toast.makeText(context, "Error: Token is null or unavailable", Toast.LENGTH_SHORT).show()
//            }


    }

    // Assuming you are inside a Fragment class

// Use the Fragment's requireActivity() instead of this@MainActivity


    fun uiKitDetails(
        transactionRequest: TransactionRequest,
        nama: String,
        phone: String,
        email: String, ) {

        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = nama
        customerDetails.phone = phone
        customerDetails.email = email
        val shippingAddress = ShippingAddress()
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        customerDetails.billingAddress = billingAddress


        transactionRequest.customerDetails = customerDetails
    }


//    fun uiKitDetails(transactionRequest: TransactionRequest ){
//
//            val customerDetails = CustomerDetails()
//            customerDetails.customerIdentifier = "Ilham Okta Alpriansyah"
//            customerDetails.phone = "0856789567"
//            customerDetails.firstName = "Ilham"
//            customerDetails.lastName = "Okta"
//            customerDetails.email = "ilhamok8@gmail.com"
//            val shippingAddress = ShippingAddress()
//            shippingAddress.address = "Desa Pekarungan"
//            shippingAddress.city = "Sidoarjo"
//            shippingAddress.postalCode = "098737"
//            customerDetails.shippingAddress = shippingAddress
//            val billingAddress = BillingAddress()
//            billingAddress.address  = "Desa Pekarungan"
//            billingAddress.city = "Sidoarjo"
//            billingAddress.postalCode = "098737"
//            customerDetails.billingAddress = billingAddress
//
//
//            transactionRequest.customerDetails = customerDetails
//        }

}








