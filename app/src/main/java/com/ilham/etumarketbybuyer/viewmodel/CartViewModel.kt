package com.ilham.etumarketbybuyer.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.ResponseAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.*
import com.ilham.etumarketbybuyer.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject



// Anotasi ini menunjukkan bahwa CartViewModel akan dikelola oleh Hilt untuk dependency injection

@HiltViewModel
//Menunjukkan bahwa ApiService dan SharedPreferences akan diinjeksikan oleh Hilt.
class CartViewModel @Inject constructor(private val api : ApiService,  private val sharedPreferences: SharedPreferences) : ViewModel() {
    //liveDataCart: Variabel ini adalah MutableLiveData yang menyimpan daftar DataAddCart

    private val liveDataCart: MutableLiveData<List<DataAddCart>> = MutableLiveData()
    //dataCart: Variabel ini adalah versi LiveData dari liveDataCart.
    val dataCart: LiveData<List<DataAddCart>> = liveDataCart

    //liveDataCart, liveDataCartUser, liveUpdateCart, deleteCart: Variabel MutableLiveData yang digunakan untuk menyimpan data yang dapat berubah.
    //dataCart, dataCartUser, dataUpdateCart, livedeletecart: Variabel LiveData yang hanya dapat dibaca oleh observer.

    //Mengirim data cart baru ke server.
    //Jika respons berhasil (response.isSuccessful), data yang diterima dari server disimpan ke liveDataCart.
    //Jika gagal, error dicatat menggunakan Log.e.

    fun postCart(token: String, postCart: DataAddCart) {
        api.postCart("Bearer $token", postCart).enqueue(object : Callback<List<DataAddCart>> {
            override fun onResponse(
                call: Call<List<DataAddCart>>,
                response: Response<List<DataAddCart>>
            ) {
                if (response.isSuccessful) {
                    liveDataCart.value = response.body()
                } else {
                    Log.e("UserViewModel", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<List<DataAddCart>>, t: Throwable) {
                Log.e("HomeViewModel", "Null Post Data Cart")
            }

        })

    }

    //savecartPreferences dan getCart: Menyimpan dan mengambil jumlah item cart dari SharedPreferences.
    //saveHargaCart dan getHargaCart: Menyimpan dan mengambil harga total cart dari SharedPreferences.
    //saveIdCart dan getIdCart: Menyimpan dan mengambil ID cart dari SharedPreferences.
    fun savecartPreferences(cart: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("cart", cart)
        editor.apply()
    }

    fun getCart(): Int {
        return sharedPreferences.getInt("cart", 0)
    }


    fun saveHargaCart(cart: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("price", cart)
        editor.apply()
    }

    fun getHargaCart(): Int {
        return sharedPreferences.getInt("price", 0)
    }


    fun saveIdCart(cart: String) {
        val editor = sharedPreferences.edit()
        editor.putString("checkout", cart)
        editor.apply()
    }

    fun getIdCart(): String? {
        return sharedPreferences.getString("checkout", " ")
    }


    private val liveDataCartUser: MutableLiveData<List<CartProduct>> =  MutableLiveData()
    val dataCartUser: LiveData<List<CartProduct>> = liveDataCartUser
    //Mengambil data cart pengguna dari server.
    //Jika respons berhasil dan data tidak null, data cart diproses dan disimpan dalam liveDataCartUser.
    //Jika gagal, error dicatat menggunakan Log.e.

    fun CartUser(token: String) {
        api.getcart("Bearer $token").enqueue(object : Callback<GetCartResponse> {
            override fun onResponse(call: Call<GetCartResponse>, response: Response<GetCartResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null && responseData.data != null) {
                        val cartProducts = responseData.data
                        liveDataCartUser.value = flattenCartData(responseData.data)
                    } else {
                        Log.e("CartUserViewModel", "Response body or products are null")
                    }
                } else {
                    Log.e("CartUserViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetCartResponse>, t: Throwable) {
                Log.e("CartViewModel3", "Failed to fetch cart data: ${t.message}")
            }
        })
    }

    //Memproses data cart dan mengubahnya menjadi daftar CartProduct.
    //Data cart diratakan untuk menyertakan detail produk, lokasi, dan biaya pengiriman.

    fun flattenCartData(dataList: List<Data>): List<CartProduct> {
        val productList = mutableListOf<CartProduct>()
        for (data in dataList) {
            val latitude = data.destination.latitude
            val longitude = data.destination.longitude
            val shippingCost = data.shippingCost
            for (product in data.products) {
                productList.add(CartProduct(product, latitude, longitude, shippingCost))
            }
        }
        return productList
    }



    private val liveUpdateCart: MutableLiveData<ResponseAddCart> = MutableLiveData()
    val dataUpdateCart: LiveData<ResponseAddCart> = liveUpdateCart

    //Mengirim data cart yang diperbarui ke server.
    //Jika respons berhasil, data yang diperbarui disimpan ke liveUpdateCart.
    //Jika gagal, error dicatat menggunakan Log.e.

    fun updatecart(token: String, UpdateCart: DataAddCart) {
        api.UpdateCart("Bearer $token", UpdateCart).enqueue(object : Callback<ResponseAddCart> {
            override fun onResponse(
                call: Call<ResponseAddCart>,
                response: Response<ResponseAddCart>
            ) {
                if (response.isSuccessful) {
                    liveUpdateCart.value = response.body()!!
                } else {
                    Log.e("CartViewModel", "Failed to update cart data: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseAddCart>, t: Throwable) {
                Log.e("CartViewModel", "Failed to update cart: ${t.message}")
            }

        })
    }


    //Menghapus semua item cart dari server.
    //Jika respons berhasil, hasil penghapusan disimpan ke deleteCart.
    //Jika gagal, error dicatat menggunakan Log.e.

    private val deleteCart: MutableLiveData<DeleteAllCartResponse> = MutableLiveData()
    val livedeletecart: LiveData<DeleteAllCartResponse> = deleteCart
    fun deletecart(token: String) {
        api.deleteProduct("Bearer $token").enqueue(object : Callback<DeleteAllCartResponse> {
            override fun onResponse(
                call: Call<DeleteAllCartResponse>,
                response: Response<DeleteAllCartResponse>
            ) {
                if (response.isSuccessful) {
                    deleteCart.value = response.body()!!
                } else {
                    Log.e("Delete Cart", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<DeleteAllCartResponse>, t: Throwable) {
                Log.e("Delete Cart", "Null Delete Data Cart")
            }

        })
    }

    fun calculateTotalPrice(cartItems: List<Product>?): Int {
        return cartItems?.sumBy { it.quantity * it.productID.price } ?: 0
    }


//    fun clearCart() {
//        liveDataCartUser.value = emptyList() // Mengosongkan LiveData
//        // Implementasi untuk menghapus data dari penyimpanan lokal, jika ada
//    }




//
    }


