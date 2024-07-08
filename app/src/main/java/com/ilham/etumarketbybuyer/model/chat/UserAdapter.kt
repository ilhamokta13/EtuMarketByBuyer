package com.ilham.etumarketbybuyer.model.chat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbybuyer.ChatActivity
import com.ilham.etumarketbybuyer.R
import com.ilham.etumarketbybuyer.databinding.ItemUserBinding

class UserAdapter(private val context: Context, private val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemUserBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.userName.text = user.fullname
        Glide.with(context).load("gs://etu-market---buyer.appspot.com/image ${user.profileImage}").placeholder(R.drawable.profile_image).into(holder.binding.userImage)

        holder.binding.layoutUser.setOnClickListener {

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId",user.userId)
            intent.putExtra("fullname",user.fullname)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }
    //a
}