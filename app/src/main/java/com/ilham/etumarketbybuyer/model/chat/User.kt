package com.ilham.etumarketbybuyer.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class User( var fullname :String = "", var profileImage:String = "", var userId:String = "" ) :Parcelable