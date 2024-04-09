package com.ilham.etumarketbybuyer.model.chat

import java.io.Serializable

data class NotificationData(
    var title:String,
    var message:String,
    val imageUrl: String? = null
)
