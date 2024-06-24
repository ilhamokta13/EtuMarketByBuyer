package com.ilham.etumarketbybuyer.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.model.product.productperid.GetProductPerId
import com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga.Data
import com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga.GetResponseTawarHarga
import com.ilham.etumarketbybuyer.model.product.tawar.UpdateHargaResponse
import com.ilham.etumarketbybuyer.model.product.tawar.UpdateProductResponse
import com.ilham.etumarketbybuyer.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProductViewModel  @Inject constructor(private val api : ApiService, private val sharedPreferences: SharedPreferences) : ViewModel() {
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
                    Log.e("TawarHarga1","${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AllProductResponse>, t: Throwable) {
                Log.e("ProductViewModel", "Data Per Shop Per Null")
            }

        })
    }


    fun saveLocation(location: String){
        val editor = sharedPreferences.edit()
        editor.putString("location", location)
        editor.apply()
    }

    fun saveLoc(location: String){
        val editor = sharedPreferences.edit()
        editor.putString("locate", location)
        editor.apply()
    }

    private val livedatatawarharga : MutableLiveData<UpdateProductResponse> = MutableLiveData()
    val datatawarharga : LiveData<UpdateProductResponse> = livedatatawarharga

    fun tawarharga(token : String, id: String, price : Int){
        api.tawarharga("Bearer $token", id, price).enqueue(object : Callback<UpdateProductResponse>{
            override fun onResponse(call: Call<UpdateProductResponse>, response: Response<UpdateProductResponse>) {
                if (response.isSuccessful){
                    livedatatawarharga.postValue(response.body())
                }
                else{
                    Log.e("TawarHarga","${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateProductResponse>, t: Throwable) {
                Log.e("TawarHarga2", "Cannot send data tawar harga")
            }

        })
    }


    private val liveposttawar : MutableLiveData<UpdateHargaResponse> = MutableLiveData()
    val dataposttawar : LiveData<UpdateHargaResponse> = liveposttawar

    fun posttawar(token: String, id: String, price: Int){
        api.posttawarharga("Bearer $token", id, price).enqueue(object :Callback<UpdateHargaResponse>{
            override fun onResponse(
                call: Call<UpdateHargaResponse>,
                response: Response<UpdateHargaResponse>
            ) {
                if (response.isSuccessful){
                   liveposttawar.postValue(response.body())
                }
                else{
                    Log.e("PostTawar","${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateHargaResponse>, t: Throwable) {
                Log.e("PostTawarHarga", "Cannot send data post tawar")
            }

        })
    }

    private val livegettawar : MutableLiveData<List<Data>> = MutableLiveData()
    val datagettawar : LiveData<List<Data>> = livegettawar

    fun gettawarharga(token: String){
        api.gettawarharga("Bearer $token").enqueue(object : Callback<GetResponseTawarHarga>{
            override fun onResponse(
                call: Call<GetResponseTawarHarga>,
                response: Response<GetResponseTawarHarga>
            ) {
                if (response.isSuccessful){
                    livegettawar.value = response.body()!!.data
                }
                else{
                    Log.e("GetTawarHarga","${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetResponseTawarHarga>, t: Throwable) {
                Log.e("GetTawarHargaNot", "Cannotgetdatatawarharga")
            }

        })
    }




    fun saveLokasi(location: String){
        val editor = sharedPreferences.edit()
        editor.putString("lokasi", location)
        editor.apply()
    }

    fun saveLock(location: String){
        val editor = sharedPreferences.edit()
        editor.putString("lock", location)
        editor.apply()
    }









}