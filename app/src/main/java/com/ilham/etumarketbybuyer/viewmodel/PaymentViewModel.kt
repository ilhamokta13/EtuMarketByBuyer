package com.ilham.etumarketbybuyer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.alltransaksi.Data
import com.ilham.etumarketbybuyer.model.alltransaksi.GetAllTransaksi
import com.ilham.etumarketbybuyer.model.alltransaksi.Product
import com.ilham.etumarketbybuyer.model.transaksi.GetTransaksiResponse
import com.ilham.etumarketbybuyer.model.transaksi.MidtransResponse
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val api : ApiService) : ViewModel() {

    private val liveMidtransResponse: MutableLiveData<GetTransaksiResponse> = MutableLiveData()
    val midtransResponse: LiveData<GetTransaksiResponse> = liveMidtransResponse



    fun postpayment(token : String, dataTransaksi: PostTransaction){
        api.posttransaksi("Bearer $token",dataTransaksi).enqueue(object:Callback<GetTransaksiResponse>{
            override fun onResponse(
                call: Call<GetTransaksiResponse>,
                response: Response<GetTransaksiResponse>
            ) {
                if (response.isSuccessful) {
                    liveMidtransResponse.value = response.body()
                } else {
                    Log.e("All Transaction", "${response.errorBody()?.string()}")

                }

            }

            override fun onFailure(call: Call<GetTransaksiResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Null Post Data Cart")
            }

        })

    }




    private val livealltransaction : MutableLiveData<List<Data>> = MutableLiveData()
    val alltransaction : LiveData<List<Data>> = livealltransaction

    fun alltransaksi(token: String){
        api.getalltransaksi("Bearer $token").enqueue(object : Callback<GetAllTransaksi>{
            override fun onResponse(
                call: Call<GetAllTransaksi>,
                response: Response<GetAllTransaksi>
            ) {
                if (response.isSuccessful) {
                    livealltransaction.value = response.body()!!.data
                } else {
                    Log.e("All Transaction", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(
                call: Call<GetAllTransaksi>,
                t: Throwable
            ) {
                Log.e("All Transaction 2", "Null Get All Cart")
            }

        })
    }


}