package com.example.chatmessenger.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatmessenger.MyApplication
import com.example.chatmessenger.SharedPrefs
import com.example.chatmessenger.Utils
import com.example.chatmessenger.adapter.MessageAdapter
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.modal.RecentChats
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.notifications.entity.NotificationData
import com.example.chatmessenger.notifications.entity.PushNotification
import com.example.chatmessenger.notifications.entity.Token
import com.example.chatmessenger.notifications.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.Locale

/**
 * ViewModel class responsible for managing UI-related data and handling user actions.
 */

class ChatAppViewModel : ViewModel() {


    // Initialize LiveData objects

    val message = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()
    val name = MutableLiveData<String>()
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    private lateinit var adapter: MessageAdapter

    // Initialize repositories

    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    var token: String? = null
    val chatlistRepo = ChatListRepo()

    // Initialize ViewModel


    init {
        getCurrentUser()
        getRecentUsers()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }



    // sendMessage
    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = MyApplication.instance.applicationContext

            var messageValue = message.value
            if (messageValue == null) {
                messageValue = ""
            }

            // Create message data

            val hashMap: Map<String, Any> = hashMapOf(
                "sender" to sender,
                "receiver" to receiver,
                "message" to messageValue,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            // Store message data in Firestore

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->

                    // Update conversation details

                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to messageValue,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    // Update user conversation

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(setHashap)

                    // Update friend conversation

                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            messageValue,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )

                    // Send notification


                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->

                            if (value != null && value.exists()) {

                                val tokenObject = value.toObject(Token::class.java)

                                token = tokenObject?.token!!

                                val loggedInUsername =
                                    mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]

                                if (messageValue.isNotEmpty() && receiver.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(loggedInUsername, messageValue), token!!
                                    ).also {
                                        sendNotification(it)
                                    }
                                } else {
                                    Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                                }
                            }

                            Log.e("ViewModel", token.toString())

                            if (taskmessage.isSuccessful) {
                                message.value = ""
                            }
                        }
                }
        }

    // getting messages
    fun getMessages(friend: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friend)
    }

    // get RecentUsers
    fun getRecentUsers(): LiveData<List<RecentChats>> {
        return chatlistRepo.getAllChatList()
    }

    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
        }
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        // Fetch current user details from Firestore

        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->

                if (value!!.exists() && value != null) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!
                    firstname.value = users.firstname!!
                    lastname.value = users.lastname!!
                    gender.value = users.gender!!

                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("username", users.username!!)
                }
            }
    }

    // Update profile

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext
        val imageUrlValue = imageUrl.value ?: ""

        // Update user profile details in Firestore

        val hashMapUser = hashMapOf(
            "username" to "${firstname.value} ${lastname.value}",
            "imageUrl" to imageUrlValue,
            "firstname" to firstname.value,
            "lastname" to lastname.value,
            "gender" to gender.value,
            "usernamelowercase" to "${firstname.value} ${lastname.value}".lowercase(Locale.getDefault()),
        )

        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .update(hashMapUser as Map<String, Any>)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(context, "Update profile succeed!", Toast.LENGTH_SHORT).show()
                }
            }

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        if (friendid != null) {
            val hashMapUpdate = hashMapOf(
                "friendsimage" to imageUrlValue,
                "name" to "${firstname.value} ${lastname.value}",
                "person" to "${firstname.value} ${lastname.value}"
            )

            // updating the chatlist and recent list message, image, etc.
            firestore.collection("Conversation$friendid")
                .document(Utils.getUidLoggedIn())
                .update(hashMapUpdate as Map<String, Any>)

            firestore.collection("Conversation${Utils.getUidLoggedIn()}")
                .document(friendid)
                .update("person", "you")
        } else {
            // Handle the case when friendid is null
        }
    }


}
