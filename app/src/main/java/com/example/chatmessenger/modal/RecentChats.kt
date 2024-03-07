package com.example.chatmessenger.modal

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class representing information about recent chats.
 *
 * @property friendid The ID of the friend involved in the chat.
 * @property friendsimage The image of the friend.
 * @property time The timestamp of the chat.
 * @property name The name of the friend.
 * @property sender The ID of the sender of the message.
 * @property message The content of the message.
 * @property person The person involved in the chat.
 * @property status The status of the chat.
 */

data class RecentChats(val friendid : String? ="",
                       val friendsimage: String? = "",
                       val time : String? = "",
                       val name: String? ="",
                       val sender: String? = "",
                       val message : String? = "",
                       val person: String? = "",
                       val status: String? ="",

                       ) : Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.writeString(friendid)
        parcel.writeString(friendsimage)
        parcel.writeString(time)
        parcel.writeString(name)
        parcel.writeString(sender)
        parcel.writeString(message)
        parcel.writeString(person)
        parcel.writeString(status)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentChats> {
        override fun createFromParcel(parcel: Parcel): RecentChats {
            return RecentChats(parcel)
        }

        override fun newArray(size: Int): Array<RecentChats?> {
            return arrayOfNulls(size)
        }
    }


}