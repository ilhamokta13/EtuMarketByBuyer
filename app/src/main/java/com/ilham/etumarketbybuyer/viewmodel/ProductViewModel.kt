package com.ilham.etumarketbybuyer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.model.product.productperid.DataPerId
import com.ilham.etumarketbybuyer.model.product.productperid.GetProductPerId
import com.ilham.etumarketbybuyer.model.product.productshopname.DataPerShop
import com.ilham.etumarketbybuyer.model.product.productshopname.GetProductspershop
import com.ilham.etumarketbybuyer.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProductViewModel  @Inject constructor(private val api : ApiService) : ViewModel() {
    private val liveDataProduct : MutableLiveData<List<DataAllProduct>> = MutableLiveData()
    val dataProduct : LiveData<List<DataAllProduct>> = liveDataProduct
    fun getAllproduct(search : String){
        api.getAllProduct(search).enqueue(object : Callback<AllProductResponse> {
            override fun onResponse(
                call: Call<AllProductResponse>,
                response: Response<AllProductResponse>
            ) {
                if (response.isSuccessful){
                    liveDataProduct.value = response.body()!!.data
                }
                else{
                    Log.e("HomeViewModel", "Cannot send data")
                }
            }

            override fun onFailure(call: Call<AllProductResponse>, t: Throwable) {
//              Log.e("BiodataViewModel","${t.errorBody()?.string()}")
                Log.e("HomeViewModel", "Data Null")

            }

        })
    }

    private val livedataperid : MutableLiveData<GetProductPerId> = MutableLiveData()
    val dataproductperid : LiveData<GetProductPerId> = livedataperid
    fun getproductperid( id: String){
        api.getProductId(id).enqueue(object : Callback<GetProductPerId>{
            override fun onResponse(
                call: Call<GetProductPerId>,
                response: Response<GetProductPerId>
            ) {
                if (response.isSuccessful){
                    livedataperid.value = response.body()
                }
                else{
                    Log.e("HomeViewModel", "Cannot send data get product per id")
                }
            }

            override fun onFailure(call: Call<GetProductPerId>, t: Throwable) {
                Log.e("HomeViewModel", "Data Id Per Null")
            }

        })

    }

    private val livedatapershop : MutableLiveData<List<DataAllProduct>> = MutableLiveData()
    val datapershop : LiveData<List<DataAllProduct>> = livedatapershop

    fun getproductpershop(search : String){
        api.getpershopname(search).enqueue(object : Callback<AllProductResponse>{
            override fun onResponse(
                call: Call<AllProductResponse>,
                response: Response<AllProductResponse>
            ) {
                if (response.isSuccessful){
                    livedatapershop.value = response.body()!!.data
                }
                else{
                    Log.e("ProductViewModel", "Cannot send data get product per shop")
                }
            }

            override fun onFailure(call: Call<AllProductResponse>, t: Throwable) {
                Log.e("ProductViewModel", "Data Per Shop Per Null")
            }

        })
    }



}