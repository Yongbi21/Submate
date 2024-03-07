package com.example.chatmessenger.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.modal.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashScreen : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        val ivNote: ImageView = findViewById(R.id.iv_note)
        ivNote.alpha = 0f
        ivNote.animate().setDuration(1500).alpha(1f).withEndAction {
            if (auth.currentUser != null) {
                var userID = auth.currentUser?.uid ?: ""
                Log.d("Baral", "$userID = ${auth.currentUser!!.uid}")

                firestore.collection("Users").document(userID)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val value = task.result
                            if (value != null && value.exists()) {
                                val users = value.toObject(Users::class.java)
                                val verified = users?.verified ?: ""
                                Log.d("Baral", verified)
                                if (verified == "true") {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                    finish()
                                }else {
                                    val i = Intent(this, SendCodeActivity::class.java)
                                    startActivity(i)
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                    finish()
                                }
                            }
                        } else {

                        }
                    }
            }else{
                val i = Intent(this@SplashScreen, SignInActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }
}
