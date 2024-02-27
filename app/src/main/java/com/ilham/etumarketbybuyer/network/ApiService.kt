package com.ilham.etumarketbybuyer.network

import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.ResponseAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.DeleteAllCartResponse
import com.ilham.etumarketbybuyer.model.cart.usercart.GetCartResponse
import com.ilham.etumarketbybuyer.model.changepass.ChangePasswordResponse
import com.ilham.etumarketbybuyer.model.changepass.DataChangePass
import com.ilham.etumarketbybuyer.model.login.LoginBody
import com.ilham.etumarketbybuyer.model.login.ResponseLogin
import com.ilham.etumarketbybuyer.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbybuyer.model.product.productperid.GetProductPerId
import com.ilham.etumarketbybuyer.model.profile.DataProfile
import com.ilham.etumarketbybuyer.model.profile.UpdateProfileResponse
import com.ilham.etumarketbybuyer.model.register.ResponseRegister
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("fullName") fullName : String,
        @Field("email") email : String,
        @Field("password") password : String,
        @Field("telp") telp : String,
        @Field("role") role:String,
    ): Call<ResponseRegister>

    @POST("user/login")
    fun login(@Body loginBody: LoginBody) : Call<ResponseLogin>

    @GET("product")
    fun getAllProduct(): Call<AllProductResponse>

    @GET("product/{id}")
    fun getProductId(
        @Path("id") id: String
    ):Call<GetProductPerId>

    @PATCH("user/reset-password")
    fun resetpassword(
        @Header("Authorization") token: String,
        @Body request: DataChangePass
    ):Call<ChangePasswordResponse>

    @POST("cart")
    fun postCart(
        @Header("Authorization") token: String,
        @Body request: DataAddCart
    ): Call <List<DataAddCart>>

    @GET("cart")
    fun getcart(
        @Header("Authorization") token: String
    ):Call<GetCartResponse>

    @PATCH("cart")
    fun UpdateCart(
        @Header("Authorization") token: String,
        @Body request: DataAddCart
    ): Call <List<DataAddCart>>

    @DELETE("cart")
    fun deleteProduct(
        @Header("Authorization") token: String
    ):Call<DeleteAllCartResponse>

    @FormUrlEncoded
    @PATCH("admin/complete-profile")
    fun updateprofile(
        @Header("Authorization") token: String,
        @Field("fullName") fullName : String,
        @Field("email") email : String,
        @Field("telp") telp : String,
        @Field("role") role:String,
        @Field("shopName") shopName:String
    ):Call<UpdateProfileResponse>



}