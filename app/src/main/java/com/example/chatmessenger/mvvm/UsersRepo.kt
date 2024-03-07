package com.example.chatmessenger.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatmessenger.Utils
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.modal.RecentChats
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.notifications.entity.Token
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {

    private val firestore = FirebaseFirestore.getInstance()


    fun getUsers(): LiveData<List<Users>> {
        val users = MutableLiveData<List<Users>>()

        val currentUserUid = Utils.getUidLoggedIn()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            val usersList = mutableListOf<Users>()

            snapshot?.documents?.forEach { userDocument ->
                val user = userDocument.toObject(Users::class.java)

                if (user?.userid != currentUserUid) {
                    usersList.add(user!!)
                }
            }

            // Filter in only users who are already friends
            firestore.collection("Add_Info")
                .document(currentUserUid)
                .collection("friend")
                .get()
                .addOnSuccessListener { friendSnapshot ->
                    val existingFriends = friendSnapshot.documents.mapNotNull { it.getString("otheruserid") }

                    val filteredUsersList = usersList.filter { user ->
                        user.userid in existingFriends
                    }

                    users.value = filteredUsersList
                }
                .addOnFailureListener { e ->
                    // Handle failure if needed
                }
        }

        return users
    }







}