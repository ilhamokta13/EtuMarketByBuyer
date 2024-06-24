package com.ilham.etumarketbybuyer.constant

import com.ilham.etumarketbybuyer.model.chat.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiNotification {
    @POST("/fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}