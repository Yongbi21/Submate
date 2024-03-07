package com.example.chatmessenger.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatmessenger.Utils
import com.example.chatmessenger.adapter.SearchUserAdapter
import com.example.chatmessenger.databinding.ActivitySearchUserBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.modal.RecentChats
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class SearchUserActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchUserBinding

    lateinit var searchAdapter: SearchUserAdapter
    lateinit var searchUserList: ArrayList<Users>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchUserList = ArrayList()
        binding.backButton.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)

             finish() }


        binding.toolbarTitleText.textSize = FontSizeHelper.getFontRecentMessageDescriptioSize(this)
        FontFamilyHelper.applyFontFamily(this, binding.toolbarTitleText)

        binding.searchText.textSize = FontSizeHelper.getFontRecentMessageDescriptioSize(this)
        FontFamilyHelper.applyFontFamily(this, binding.searchText)


        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                performUserSearch(s.toString().lowercase(Locale.ROOT))
            }
        })

        searchAdapter = SearchUserAdapter(searchUserList)
        binding.recyclerView.adapter = searchAdapter

        performUserSearch("")

    }

    private fun performUserSearch(searchName: String) {
        if (searchName.isEmpty() || searchName == ""){
            searchUserList.clear()
            searchAdapter.notifyDataSetChanged()
            return;
        }

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseFirestore.getInstance().collection("Users")
            .orderBy("usernamelowercase")
            .startAt(searchName)
            .endAt(searchName + "\uf8ff")

        query.get()
            .addOnSuccessListener { querySnapshot ->
                searchUserList.clear()
                querySnapshot?.forEach { document ->
                    val chatlistmodel = document.toObject(Users::class.java)
                    if (firebaseUser != null && firebaseUser.uid != chatlistmodel?.userid && chatlistmodel.verified == "true") {
                        chatlistmodel?.let {
                            searchUserList.add(it)
                        }
                    }
                }
                searchAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("SearchUserActivity", "Error getting documents: ", e)
            }
    }


}