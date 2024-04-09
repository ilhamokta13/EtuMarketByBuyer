package com.ilham.etumarketbybuyer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ilham.etumarketbybuyer.databinding.FragmentPaymentBinding
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.PaymentViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import com.midtrans.sdk.corekit.models.ItemDetails
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentFragment : Fragment() {

    lateinit var binding: FragmentPaymentBinding
    lateinit var pref : SharedPreferences
    lateinit var paymentVm : PaymentViewModel
    lateinit var productVm : ProductViewModel
    lateinit var cartVm : CartViewModel
    private lateinit var idProduct: String
    private lateinit var idProductCart: String
    private var titleAd:String? = null
    private var selectedPaymentMethod: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        cartVm = ViewModelProvider(this).get(CartViewModel::class.java)


//        val getData = arguments?.getSerializable("checkout") as? HashMap<String, ArrayList<Product>>

//        getData?.let { uniqueProductsMap ->
//
//        }



       val getData= arguments?.getSerializable("checkout") as Product

        idProduct = getData.productID.id


        val harga = getData.productID.price
        val jumlah = getData.quantity
        val total =  harga*jumlah

        binding.tvTotalHarga.text = total.toString()
        binding.tvJumlahCart.text = jumlah.toString()

        productVm.getproductperid(idProduct)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Toast.makeText(context,"${transactionResult?.transactionId}", Toast.LENGTH_LONG).show()
                }
            }
        }

        paymentVm.midtransResponse.observe(viewLifecycleOwner){
            if (it.message == "Transaksi berhasil dibuat") {

                val tokenmidtrans = it.midtransResponse.token
                val redirectUrl = it.midtransResponse.redirectUrl


                Log.d("Payment Midtrans", "Midtrans :$tokenmidtrans")
                val redirecturl = it.midtransResponse.redirectUrl

                // Cetak log atau tampilkan toast jika perlu
                Log.d("Payment Redirect", "Redirect URL: $redirectUrl")

                // Buka redirect URL
                openRedirectUrl(redirectUrl)



            } else {
                Toast.makeText(context, "Response Failed", Toast.LENGTH_SHORT).show()
            }

        }




        observeDetailProduct()

        binding.btnbayar.setOnClickListener {
            val getData = arguments?.getSerializable("checkout") as Product
            idProductCart = getData.productID.id

            val namaproduk = getData.productID.nameProduct


            val jmlcart = getData.quantity

//        val belanja = jmlcart

//        val harga = getData.productID.price
//
            val harga = getData.productID.price

            val keseluruhanharga = jmlcart * harga.toDouble()


            binding.tvJumlahCart.text = jmlcart.toString()



//            val dataCart = PostTransaction(listOf(idProductCart), listOf(jmlcart), listOf(keseluruhanharga.toInt()))
            val token = pref.getString("token", "").toString()

            binding.tvTotalHarga.text = keseluruhanharga.toString()

//            paymentVm.postpayment(token, dataCart)

            val transactionRequest = TransactionRequest(
                "ETU-Market-" + System.currentTimeMillis().toShort() + "",
                keseluruhanharga
            )

            val detail = ItemDetails(idProductCart,harga.toDouble(),jmlcart,namaproduk)
            val itemDetails = ArrayList<ItemDetails>()

            itemDetails.add(detail)





            uiKitDetails(transactionRequest,"Ilham", "0856373829", "iljhamok@gmail.com")

            transactionRequest.itemDetails = itemDetails

            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(context)



        }


//        SdkUIFlowBuilder.init()
//            .setClientKey("SB-Mid-client-o40DgvDVP2nSe_nD")
//            .setContext(context)
//            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->
//
//            })
//
//
//            .setMerchantBaseUrl("https://app.sandbox.midtrans.com/snap/v3/redirection/")
//
//
//            .enableLog(true)
//            .setColorTheme(
//                CustomColorTheme(
//                    "#FFE51255",
//                    "#B61548",
//                    "#FFE51255"
//                )
//            ) // set theme. it will replace theme on snap theme on MAP ( optional)
//            .setLanguage("id")
//            .buildSDK();

//        UiKitApi.Builder()
//            .withMerchantClientKey("SB-Mid-client-o40DgvDVP2nSe_nD") // client_key is mandatory
//            .withContext(requireContext()) // context is mandatory
//            .withMerchantUrl(BASE_URL) // set transaction finish callback (sdk callback)
//            .enableLog(true) // enable sdk log (optional)
//
//            .withFontFamily(ResourcesCompat.getFont(requireContext(), R.font.metropolis_semibold).toString())
//            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
//            .build()
//        setLocaleNew("en") //`en` for English and `id` for Bahasa


        // function to set the SDK language




        val hintTitle = resources.getStringArray(R.array.MenuPembayaran)

        formTitle(hintTitle)

        binding.uploadCategory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedPaymentMethod = hintTitle[position].toString()
        }

//        UiKitApi.getDefaultInstance().startPaymentUiFlow(
//            requireActivity(), // Activity
//            launcher,
////            tokenmidtrans// ActivityResultLauncher
//            // Snap Token
//        )






    }

    private fun openRedirectUrl(redirectUrl: String) {
        val bundle = Bundle()

        bundle.putString("URL",redirectUrl).apply {
           findNavController().navigate(R.id.action_paymentFragment_to_webViewFragment,bundle)
        }

    }





    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }



    private fun formTitle(hintTitle: Array<String>) {
        binding.uploadCategory.apply {
            val adapterTitle = ArrayAdapter(requireContext(), R.layout.dropdown_item, hintTitle)
            setAdapter(adapterTitle)
            hint = "Title"
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                titleAd = adapterTitle.getItem(position).toString()
            }
        }
    }


    private fun observeDetailProduct() {
        productVm.dataproductperid.observe(viewLifecycleOwner) { detailproduct ->

            binding.apply {
                if (detailproduct != null) {
                    binding.tvNamaProduk.text = detailproduct.data.nameProduct
                    binding.tvJenisProduk.text = detailproduct.data.category
                    binding.tvNameProduk.text = detailproduct.data.description
                    binding.tvHarga.text = detailproduct.data.price.toString()
                    binding.tvName.text = detailproduct.data.sellerID.fullName
                    binding.tvHarga.text = detailproduct.data.price.toString()





                }


            }






        }









    }




    fun uiKitDetails(transactionRequest: TransactionRequest, nama : String, phone : String,  email : String,){

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


}


