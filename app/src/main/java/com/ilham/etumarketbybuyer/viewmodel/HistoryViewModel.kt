package com.ilham.etumarketbybuyer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.status.PostUpdateStatus
import com.ilham.etumarketbybuyer.model.status.ResponseUpdateStatus
import com.ilham.etumarketbybuyer.model.transaksi.riwayat.DataHistory
import com.ilham.etumarketbybuyer.model.transaksi.riwayat.GetRiwayatResponse
import com.ilham.etumarketbybuyer.network.ApiService
import com.ilham.etumarketbybuyer.pengiriman.PengirimanResponse
import com.ilham.etumarketbybuyer.pengiriman.PostPengiriman
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val api : ApiService) : ViewModel() {

    private val livehistory: MutableLiveData<List<DataHistory>> = MutableLiveData()
    val datahistory: LiveData<List<DataHistory>> = livehistory

    fun gethistory(token:String){
        api.gethistory("Bearer $token").enqueue(object : Callback<GetRiwayatResponse>{
            override fun onResponse(
                call: Call<GetRiwayatResponse>,
                response: Response<GetRiwayatResponse>
            ) {
                if (response.isSuccessful) {
                    val historyList = mutableListOf<DataHistory>()
                    response.body()?.data?.forEach { transaction ->
                        // Memproses setiap transaksi untuk menambahkannya ke daftar riwayat
                        transaction.products.forEach{ product->
                            // Membuat objek DataHistory baru untuk setiap produk dalam transaksi
                            val dataHistory = DataHistory(
                                transaction.id,
                                transaction.kodeTransaksi,
                                listOf(product),
                                transaction.status,// Menggunakan listOf(product) untuk menambahkan produk ke dalam list products
                                transaction.total,
                                transaction.user,
                                transaction.v  // Memastikan properti __v juga disertakan jika diperlukan
                            )
                            // Menambahkan objek DataHistory ke daftar riwayat
                            historyList.add(dataHistory)
                        }
                    }
                    livehistory.value = historyList
                } else {
                    Log.e("UserViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetRiwayatResponse>, t: Throwable) {
                Log.e("History", "Null Post Data History")
            }

        })
    }


    private val liveUpdateStatus : MutableLiveData<ResponseUpdateStatus> = MutableLiveData()
    val dataupdatestatus : LiveData<ResponseUpdateStatus> = liveUpdateStatus

    fun updateStatus(token: String, kode_transaksi: RequestBody, image: MultipartBody.Part, productID: RequestBody, status: RequestBody) {
        api.poststatus("Bearer $token", kode_transaksi, image, productID, status).enqueue(object : Callback<ResponseUpdateStatus> {
            override fun onResponse(call: Call<ResponseUpdateStatus>, response: Response<ResponseUpdateStatus>) {
                if (response.isSuccessful) {
                    liveUpdateStatus.value = response.body()
                } else {
                    Log.e("HistoryViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseUpdateStatus>, t: Throwable) {
                Log.e("HistoryViewModel", "Null Post Update Status")
            }
        })
    }

    private val livepostlocpengiriman : MutableLiveData<PengirimanResponse> = MutableLiveData()
    val datalocpengiriman : LiveData<PengirimanResponse> = livepostlocpengiriman

    fun kirimlokasi(token: String, postPengiriman: PostPengiriman){
        api.kirimlokasibarang("Bearer $token", postPengiriman).enqueue(object : Callback<PengirimanResponse>{
            override fun onResponse(
                call: Call<PengirimanResponse>,
                response: Response<PengirimanResponse>
            ) {
                if (response.isSuccessful) {
                    livepostlocpengiriman.value = response.body()
                } else {
                    Log.e("SharelocViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PengirimanResponse>, t: Throwable) {
                Log.e("sharelocVm", "Null Post Update Shareloc")
            }

        })
    }



}