package com.example.chatmessenger

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.example.chatmessenger.notifications.FirebaseService.Companion.token
import com.example.chatmessenger.notifications.entity.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Utils {

    // Companion object to hold static properties
    companion object {
        // Get the application context
        @SuppressLint("StaticFieldLeak")
        val context = MyApplication.instance.applicationContext

        // Get the instance of Firestore database
        @SuppressLint("StaticFieldLeak")
        val firestore = FirebaseFirestore.getInstance()

        // Get the instance of Firebase Authentication
        private val auth = FirebaseAuth.getInstance()
        // Variable to hold the current user ID
        private var userid: String = ""
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2

        // Function to get the logged-in user ID
        fun getUidLoggedIn(): String {
            if (auth.currentUser != null) {
                userid = auth.currentUser!!.uid
            }
            return userid
        }

        // Function to get the current time
        fun getTime(): String {
            // Specify the time zone for the Philippines
            val timeZone = TimeZone.getTimeZone("Asia/Manila")

            // Create a SimpleDateFormat instance with the desired time zone and locale
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            formatter.timeZone = timeZone

            // Get the current date and time
            val currentDate = Date(System.currentTimeMillis())

            // Format the date and time and return it as a string
            return formatter.format(currentDate)
        }
    }
}
