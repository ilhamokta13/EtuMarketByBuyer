package com.ilham.etumarketbybuyer.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.DeleteAllCartResponse
import com.ilham.etumarketbybuyer.model.cart.usercart.GetCartResponse
import com.ilham.etumarketbybuyer.model.cart.usercart.Product
import com.ilham.etumarketbybuyer.model.changepass.DataChangePass
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val api : ApiService,  private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val liveDataCart : MutableLiveData<List<DataAddCart>> = MutableLiveData()
    val dataCart : LiveData<List<DataAddCart>> = liveDataCart

    fun postCart(token:String, postCart : DataAddCart){
        api.postCart("Bearer $token", postCart).enqueue(object: Callback<List<DataAddCart>>{
            override fun onResponse(
                call: Call<List<DataAddCart>>,
                response: Response<List<DataAddCart>>
            ) {
                if (response.isSuccessful){
                    liveDataCart.value = response.body()
                }
                else{
                    Log.e("UserViewModel", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<List<DataAddCart>>, t: Throwable) {
                Log.e("HomeViewModel", "Null Post Data Cart")
            }

        })

    }

    fun savecartPreferences(cart: Int){
        val editor = sharedPreferences.edit()
        editor.putInt("cart",cart)
        editor.apply()
    }

    fun getCart():Int{
        return sharedPreferences.getInt("cart",0)
    }


    fun saveHargaCart(cart: Int){
        val editor = sharedPreferences.edit()
        editor.putInt("price",cart)
        editor.apply()
    }

    fun getHargaCart():Int{
        return sharedPreferences.getInt("price",0)
    }


    fun saveIdCart(cart: String){
        val editor = sharedPreferences.edit()
        editor.putString("idcart", cart)
        editor.apply()
    }

    fun getIdCart():String?{
        return sharedPreferences.getString("idcart"," ")
    }




    private val liveDataCartUser : MutableLiveData<List<Product>> = MutableLiveData()
    val dataCartUser : LiveData<List<Product>> = liveDataCartUser
    fun CartUser(token: String){
        api.getcart("Bearer $token").enqueue(object : Callback<GetCartResponse>{
            override fun onResponse(
                call: Call<GetCartResponse>,
                response: Response<GetCartResponse>
            ) {
                if (response.isSuccessful){
                    liveDataCartUser.value = response.body()!!.data.products
                }
                else{
                    Log.e("CartUserViewModel", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<GetCartResponse>, t: Throwable) {
                Log.e("CartViewModel", "Null Post Data Cart")
            }

        })
    }


    private val liveUpdateCart : MutableLiveData<List<DataAddCart>> = MutableLiveData()
    val dataUpdateCart : LiveData<List<DataAddCart>> = liveUpdateCart

    fun updatecart(token: String, UpdateCart : DataAddCart){
        api.UpdateCart("Bearer $token", UpdateCart).enqueue(object : Callback<List<DataAddCart>>{
            override fun onResponse(
                call: Call<List<DataAddCart>>,
                response: Response<List<DataAddCart>>
            ) {
                if (response.isSuccessful){
                    liveUpdateCart.value = response.body()!!
                }
                else{
                    Log.e("CartUserViewModel", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<List<DataAddCart>>, t: Throwable) {
                Log.e("CartViewModel", "Null Data Cart")
            }

        })
    }

    private val deleteCart : MutableLiveData<DeleteAllCartResponse> = MutableLiveData()
    val livedeletecart : LiveData<DeleteAllCartResponse> = deleteCart
    fun deletecart(token : String){
        api.deleteProduct("Bearer $token").enqueue(object : Callback<DeleteAllCartResponse>{
            override fun onResponse(
                call: Call<DeleteAllCartResponse>,
                response: Response<DeleteAllCartResponse>
            ) {
                if (response.isSuccessful){
                    deleteCart.value = response.body()!!
                }
                else{
                    Log.e("Delete Cart", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<DeleteAllCartResponse>, t: Throwable) {
                Log.e("Delete Cart", "Null Delete Data Cart")
            }

        })
    }

    fun calculateTotalPrice(cartItems: List<Product>): Int {
        return cartItems.sumBy { it.quantity * it.productID.price }
    }
}