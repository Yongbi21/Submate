package com.example.chatmessenger.modal

import android.os.Parcel
import android.os.Parcelable


data class Users(

    val userid: String? = "",
    val status : String? = "",
    val imageUrl : String? = "",
    val username: String? = "",
    val usernamelowercase: String? = "",
    val useremail : String? ="",
    val firstname : String? ="",
    val lastname : String? ="",
    val gender : String? ="",
    val code : String? = "",
    val verified : String? = "",






): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(username)
        parcel.writeString(usernamelowercase)
        parcel.writeString(useremail)
        parcel.writeString(firstname)
        parcel.writeString(lastname)
        parcel.writeString(gender)
        parcel.writeString(code)
        parcel.writeString(verified)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }


}