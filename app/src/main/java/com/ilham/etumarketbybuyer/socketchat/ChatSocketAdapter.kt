package com.ilham.etumarketbybuyer.socketchat

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ilham.etumarketbybuyer.model.chat.Chat

class ChatSocketAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_SELF = 1
    private val ITEM_OTHER = 2

    private val diffcallback = object : DiffUtil.ItemCallback<ChatSocket>() {
        override fun areItemsTheSame(oldItem: ChatSocket, newItem: ChatSocket): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatSocket, newItem: ChatSocket): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffcallback)

    fun submitChat(chats: List<ChatSocket>) {
        differ.submitList(chats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}