package com.ilham.etumarketbybuyer.network

import com.ilham.etumarketbybuyer.model.alltransaksi.GetAllTransaksi
import com.ilham.etumarketbybuyer.model.cart.DataAddCart
import com.ilham.etumarketbybuyer.model.cart.ResponseAddCart
import com.ilham.etumarketbybuyer.model.cart.usercart.DeleteAllCartResponse
import com.ilham.etumarketbybuyer.model.cart.usercart.GetCartResponse
import com.ilham.etumarketbybuyer.model.changepass.*
import com.ilham.etumarketbybuyer.model.login.LoginBody
import com.ilham.etumarketbybuyer.model.login.ResponseLogin
import com.ilham.etumarketbybuyer.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbybuyer.model.product.productperid.GetProductPerId
import com.ilham.etumarketbybuyer.model.product.tawar.GetTawarHarga.GetResponseTawarHarga
import com.ilham.etumarketbybuyer.model.product.tawar.UpdateHargaResponse
import com.ilham.etumarketbybuyer.model.product.tawar.UpdateProductResponse
import com.ilham.etumarketbybuyer.model.profile.GetProfileResponse
import com.ilham.etumarketbybuyer.model.profile.UpdateProfileResponse
import com.ilham.etumarketbybuyer.model.register.ResponseRegister
import com.ilham.etumarketbybuyer.model.status.ResponseUpdateStatus
import com.ilham.etumarketbybuyer.model.transaksi.GetTransaksiResponse
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbybuyer.model.transaksi.riwayat.GetRiwayatResponse
import com.ilham.etumarketbybuyer.pengiriman.PengirimanResponse
import com.ilham.etumarketbybuyer.pengiriman.PostPengiriman
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Field("shopName") shopName : String
    ): Call<ResponseRegister>

    @POST("user/login")
    fun login(@Body loginBody: LoginBody) : Call<ResponseLogin>

    @POST("user/forgot-password")
    fun forgotpass(
        @Body postForgotPass: PostForgotPass) : Call<ResponseForgotPass>

    @POST("user/new-password")
    fun newpass(
        @Body postNewPassword: PostNewPassword) : Call<ResponseNewPass>


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
        @Field("telp") telp : String,
        @Field("role") role:String,
        @Field("shopName") shopName:String,
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
    ):Call<AllProductResponse>

    @GET("/transaksi/getUserTransaksi")
    fun gethistory(
        @Header("Authorization") token: String
    ):Call<GetRiwayatResponse>

    @Multipart
    @PATCH("/transaksi/updateStatus")
    fun poststatus(
        @Header("Authorization") token: String,
        @Part("kode_transaksi") kode_transaksi: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("productID") productID: RequestBody,
        @Part("status") status: RequestBody
    ): Call<ResponseUpdateStatus>


    @GET("/user/profile")
    fun getprofile(
        @Header("Authorization") token: String
    ):Call<GetProfileResponse>

    @FormUrlEncoded
    @PATCH("admin/complete-profile")
    fun updateprofileemail(
        @Header("Authorization") token: String,
        @Field("email") email : String,
    ):Call<UpdateProfileResponse>


    @FormUrlEncoded
    @PATCH("product/{id}/price")
    fun tawarharga(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Field("price") price : Int,

    ):Call<UpdateProductResponse>


    @POST("transaksi/calculate-shipping-cost")
    fun kirimlokasibarang(
        @Header("Authorization") token: String,
        @Body postPengiriman: PostPengiriman
    ):Call<PengirimanResponse>

    @FormUrlEncoded
    @POST("product/{id}/offer")
    fun posttawarharga(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Field("price") price : Int,
    ):Call<UpdateHargaResponse>


    @GET("product/offers/status")
    fun gettawarharga(
        @Header("Authorization") token: String,
    ):Call<GetResponseTawarHarga>

 






}