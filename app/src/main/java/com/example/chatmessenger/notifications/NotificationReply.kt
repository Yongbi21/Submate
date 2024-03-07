package com.example.chatmessenger.notifications

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.chatmessenger.R
import com.example.chatmessenger.SharedPrefs
import com.example.chatmessenger.Utils
import com.example.chatmessenger.modal.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class NotificationReply  : BroadcastReceiver(){

    // Firebase Firestore instance
    val firestor = FirebaseFirestore.getInstance()

    // This method is triggered when a reply is received.
    override fun onReceive(context: Context?, intent: Intent?) {

        // Get the notification manager service
        val notificationManager : NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Extract the remote input from the intent
        val remoteInput = RemoteInput.getResultsFromIntent(intent)

        // Check if the remote input is not null
        if (remoteInput!=null){

            // Retrieve the replied text from the remote input
            val repliedText = remoteInput.getString("KEY_REPLY_TEXT")

            // Get necessary data from shared preferences
            val mysharedPrefs = SharedPrefs(context)
            val friendid = mysharedPrefs.getValue("friendid")
            val chatroomid = mysharedPrefs.getValue("chatroomid")
            val friendname = mysharedPrefs.getValue("friendname")
            val friendimage = mysharedPrefs.getValue("friendimage")

            // Create a hash map for the message data
            val hashMap = hashMapOf<String, Any>(
                "sender" to Utils.getUidLoggedIn(),
                "time" to Utils.getTime(),
                "receiver" to friendid!!,
                "message" to repliedText!!
            )

            // Add the message to Firestore
            firestor.collection("Messages").document(chatroomid!!)
                .collection("chats").document(Utils.getTime()).set(hashMap).addOnCompleteListener {
                    // Handle completion if needed
                }

            // Set up data for conversation collection
            val setHashap = hashMapOf<String, Any>(
                "friendid" to friendid,
                "time" to Utils.getTime(),
                "sender" to Utils.getUidLoggedIn(),
                "message" to repliedText,
                "friendsimage" to friendimage!!,
                "name" to friendname!!,
                "person" to "you"
            )

            // Add data to the conversation collection
            firestor.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid)
                .set(setHashap)

            // Update data in the conversation collection
            val updateHashMap = hashMapOf<String, Any>(
                "message" to repliedText, "time" to Utils.getTime(), "person" to friendname!!
            )

            firestor.collection("Conversation${friendid}").document(Utils.getUidLoggedIn())
                .update(updateHashMap)

            // Get the notification ID for the replied notification
            val sharedCustomPref = SharedPrefs(context)
            val replyid : Int = sharedCustomPref.getIntValue("values", 0)

            // Build the replied notification
            val repliedNotification  =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.submate_icon)
                    .setContentText("Reply Sent").build()

            // Notify the system about the replied notification
            notificationManager.notify(replyid, repliedNotification)
        }
    }
}
