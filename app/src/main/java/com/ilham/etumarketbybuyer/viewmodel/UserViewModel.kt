package com.ilham.etumarketbybuyer.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.ilham.etumarketbybuyer.model.changepass.ChangePasswordResponse
import com.ilham.etumarketbybuyer.model.changepass.DataChangePass
import com.ilham.etumarketbybuyer.model.login.LoginBody
import com.ilham.etumarketbybuyer.model.login.ResponseLogin
import com.ilham.etumarketbybuyer.model.profile.DataProfile
import com.ilham.etumarketbybuyer.model.profile.UpdateProfileResponse
import com.ilham.etumarketbybuyer.model.register.ResponseRegister
import com.ilham.etumarketbybuyer.network.ApiService
import com.ilham.etumarketbybuyer.room.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val api : ApiService, ) : ViewModel() {
    private val _responseregister : MutableLiveData<ResponseRegister> = MutableLiveData()
    val responseregister : LiveData<ResponseRegister> = _responseregister

    private val _toastLogin = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastLogin

    private val _responselogin : MutableLiveData<ResponseLogin> = MutableLiveData()
    val responselogin : LiveData<ResponseLogin> = _responselogin

    fun postregist(fullName : String, email : String, password : String, telp : String, role : String,){
        api.register(fullName, email, password, telp, role,).enqueue(object :
            Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                if (response.isSuccessful) {
                    _responseregister.value = response.body()

                } else {
                    Log.e("UserViewModel", "Cannot get data")
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                Log.e("UserViewModel", "Cannot get data")
            }

        })

    }

    fun postlogin(loginBody: LoginBody){
        api.login(loginBody).enqueue(object:Callback<ResponseLogin>{
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    _responselogin.value = response.body()

                }
                else {
//                   _responselogin.value = LoginResponse(
//                       "",
//                       "",
//                       false
//                   )
//
                    _toastLogin.value="Login Failed"
                    Log.e("UserViewModel", "Cannot get data")
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Log.e("UserViewModel", "Cannot get data")
            }

        })
    }

    private val _responsechangepass : MutableLiveData<ChangePasswordResponse> = MutableLiveData()
    val responsechangepass : LiveData<ChangePasswordResponse> = _responsechangepass

    fun changepass(token : String, resetpass: DataChangePass){
        api.resetpassword("Bearer $token", resetpass).enqueue(object : Callback<ChangePasswordResponse>{
            override fun onResponse(
                call: Call<ChangePasswordResponse>,
                response: Response<ChangePasswordResponse>
            ) {
                if (response.isSuccessful) {
                    _responsechangepass.value = response.body()

                } else {
                    Log.e("UserViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                Log.e("UserViewModel 2", "Cannot get data change pass")
            }

        })
    }

    private val _responseupdateprofile : MutableLiveData<DataProfile> = MutableLiveData()
    val responseupdateprofile : LiveData<DataProfile> = _responseupdateprofile

    fun updateprofile(token: String, fullName: String, email: String, telp: String, role: String, shopName:String){
        api.updateprofile("Bearer $token", fullName, email, telp, role, shopName).enqueue(object : Callback<UpdateProfileResponse>{
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if (response.isSuccessful) {
                    _responseupdateprofile.value = response.body()!!.data

                } else {
                    Log.e("Update Profile", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Log.e("Update Profile Null", "Cannot get data update profile")
            }

        })

    }

//    //get login state
//    fun getLoginState(): LiveData<Boolean> {
//        return prefManager.getLoginState().asLiveData()
//    }
//
//    fun saveLoginState(state:Boolean){
//        viewModelScope.launch {
//            prefManager.saveLoginState(state)
//        }
//    }






}