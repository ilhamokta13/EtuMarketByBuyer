package com.ilham.etumarketbybuyer.model.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ilham.etumarketbybuyer.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ChatAdapter(private val context: Context, private val chatList:ArrayList<Chat>)
    : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
        val imgMessage: ImageView = view.findViewById(R.id.imgMessage) // Add ImageView for images

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        if (chat.imageUrl.isNullOrEmpty()) {
            // Text message
            holder.txtUserName.visibility = View.VISIBLE
            holder.imgMessage.visibility = View.GONE
            holder.txtUserName.text = chat.message
        } else {
            // Image message
            holder.txtUserName.visibility = View.GONE
            holder.imgMessage.visibility = View.VISIBLE
            Picasso.get().load(chat.imageUrl).into(holder.imgMessage)

            holder.imgMessage.setOnClickListener {
                // Call a method to view or download the image
                viewOrDownloadImage(chat.imageUrl)

            }
        }
//        Glide.with(context).load(chat.profileImage).placeholder(R.drawable.profile_image).into(holder.imgUser)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].senderId == firebaseUser!!.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }

    }

    private fun viewOrDownloadImage(imageUrl: String) {
        // You can implement the logic to view or download the image here
        // For example, open the image in a new activity or download it to the device
        // You can use an image loading library like Glide or Picasso for a smoother experience
        // For simplicity, you can use the following code to open the image in the default browser:

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(imageUrl)
        context.startActivity(intent)
    }

    private fun openImageWithExternalApp(imageFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }
}