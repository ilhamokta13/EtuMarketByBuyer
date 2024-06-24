package com.ilham.etumarketbybuyer.model.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ilham.etumarketbybuyer.ChatActivity
import com.ilham.etumarketbybuyer.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.gms.maps.model.LatLng
import com.ilham.etumarketbybuyer.MapsActivity

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>)
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        if (chat.imageUrl.isNullOrEmpty()) {
            holder.txtUserName.visibility = View.VISIBLE
            holder.imgMessage.visibility = View.GONE
            holder.txtUserName.text = chat.message

            // Add Linkify to detect web URLs
//            Linkify.addLinks(holder.txtUserName, Linkify.WEB_URLS)

            holder.txtUserName.setOnClickListener {
                val message = holder.txtUserName.text.toString()
                val url = extractGoogleMapsUrl(message)
                if (url != null) {
                    val uri = Uri.parse(url)
                    val latLng = uri.getQueryParameter("query")
                    if (latLng != null) {
                        val appUri = Uri.Builder()
                            .scheme("myapp")
                            .authority("maps")
                            .appendQueryParameter("query", latLng)
                            .build()
                        val intent = Intent(context, MapsActivity::class.java).apply {
                            data = appUri
                        }
                        context.startActivity(intent)
                    }
                } else {
//                    // Open other URLs in the default browser
//                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(message))
//                    context.startActivity(browserIntent)
                }
            }
        } else {
            holder.txtUserName.visibility = View.GONE
            holder.imgMessage.visibility = View.VISIBLE
            Picasso.get().load(chat.imageUrl).into(holder.imgMessage)

            holder.imgMessage.setOnClickListener {
                // Call a method to view or download the image
                viewOrDownloadImage(chat.imageUrl)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    // Fungsi untuk menyalin teks ke Clipboard
    private fun copyToClipboard(text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    // Fungsi untuk menampilkan pesan toast
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].senderId == firebaseUser!!.uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

    private fun viewOrDownloadImage(imageUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(imageUrl)
        context.startActivity(intent)
    }

    private fun extractGoogleMapsUrl(text: String): String? {

        val regex = "https://www.google.com/maps/search/[^\"]+".toRegex()
        val match = regex.find(text)
        return match?.value
    }


}
