package com.ilham.etumarketbybuyer.model.chat

data class Chat(var senderId:String = "",
                var receiverId:String = "",
                var message:String = "",
                val imageUrl: String? = null,
                val audioUrl: String? = null,
                val audioDuration: Long? = null)
