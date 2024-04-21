package com.ilham.etumarketbybuyer.network

import com.ilham.etumarketbybuyer.model.alltransaksi.GetAllTransaksi
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
import com.ilham.etumarketbybuyer.model.product.productshopname.GetProductspershop
import com.ilham.etumarketbybuyer.model.profile.GetProfileResponse
import com.ilham.etumarketbybuyer.model.profile.UpdateProfileResponse
import com.ilham.etumarketbybuyer.model.register.ResponseRegister
import com.ilham.etumarketbybuyer.model.status.PostUpdateStatus
import com.ilham.etumarketbybuyer.model.status.ResponseUpdateStatus
import com.ilham.etumarketbybuyer.model.transaksi.GetTransaksiResponse
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.model.transaksi.riwayat.GetRiwayatResponse
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
    fun getAllProduct(
        @Query("search") search:String
    ): Call<AllProductResponse>

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
    ): Call <ResponseAddCart>

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

    @POST("transaksi/create")
    fun posttransaksi(
        @Header("Authorization") token: String,
        @Body request: PostTransaction
    ):Call<GetTransaksiResponse>

    @GET("transaksi/get")
    fun getalltransaksi(
        @Header("Authorization") token: String
    ):Call<GetAllTransaksi>

    @GET("/product/shop/{shopName}")
    fun getpershopname(
        @Path("shopName") shopName: String,
    ):Call<GetProductspershop>

    @GET("/transaksi/getUserTransaksi")
    fun gethistory(
        @Header("Authorization") token: String
    ):Call<GetRiwayatResponse>

    @PATCH("/transaksi/updateStatus")
    fun poststatus(
        @Header("Authorization") token: String,
        @Body postUpdateStatus: PostUpdateStatus
    ):Call<ResponseUpdateStatus>

    @GET("/user/profile")
    fun getprofile(
        @Header("Authorization") token: String
    ):Call<GetProfileResponse>

 






}