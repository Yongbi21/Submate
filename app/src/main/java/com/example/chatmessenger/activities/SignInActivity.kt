@file:Suppress("DEPRECATION")

package com.example.chatmessenger.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.databinding.ActivitySignInBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.helper.GmailSender
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity for user sign-in.
 */

class SignInActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding
    lateinit var binding: ActivitySignInBinding

    /**
     * Refresh text size and font family for all relevant views.
     */

    private fun refreshTextSize() {
        binding.loginetemail.textSize = FontSizeHelper.getFontDescriptionSize(applicationContext)
        binding.loginetpassword.textSize = FontSizeHelper.getFontDescriptionSize(applicationContext)
        binding.signInTextToSignUp.textSize =
            FontSizeHelper.getFontLoginSmallDescriptionSize(applicationContext)
        binding.Welcometxt.textSize =
            FontSizeHelper.getFontLoginSmallDescriptionSize(applicationContext)
        binding.titleText.textSize = FontSizeHelper.getFontTitleSize(applicationContext)

        FontFamilyHelper.applyFontFamily(applicationContext, binding.loginetemail)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.loginetpassword)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.signInTextToSignUp)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.Welcometxt)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.titleText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        refreshTextSize()

        auth = FirebaseAuth.getInstance()


        if (auth.currentUser != null) {


            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }

        progressDialog = ProgressDialog(this)

        binding.signInTextToSignUp.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)



            startActivity(Intent(this, SignUpActivity::class.java))


        }


        binding.settingsButton.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)


            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        }


        binding.loginButton.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)

            VibrationUtil.vibrate(applicationContext)

            email = binding.loginetemail.text.toString()
            password = binding.loginetpassword.text.toString()




            if (binding.loginetemail.text.isEmpty()) {

                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()


            }


            if (binding.loginetpassword.text.isEmpty()) {

                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()


            }


            if (binding.loginetemail.text.isNotEmpty() && binding.loginetpassword.text.isNotEmpty()) {


                signIn(password, email)


            }


        }


    }


    override fun onResume() {
        super.onResume()
        refreshTextSize()
    }

    private lateinit var auth1: FirebaseAuth
    val firestore = FirebaseFirestore.getInstance()

    private fun signIn(password: String, email: String) {
        progressDialog.show()
        progressDialog.setMessage("Signing In")

        auth1 = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {


            if (it.isSuccessful) {

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
                                    progressDialog.dismiss()
                                    if (verified == "true") {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    } else {
                                        progressDialog.dismiss()
                                        startActivity(Intent(this, SendCodeActivity::class.java))
                                        finish()
                                    }
                                }
                            } else {

                            }
                        }
                }


            } else {

                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()


            }


        }.addOnFailureListener { exception ->


            when (exception) {

                is FirebaseAuthInvalidCredentialsException -> {

                    Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT)
                        .show()


                }

                else -> {

                    // other exceptions
                    Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT)
                        .show()


                }


            }


        }


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        progressDialog.dismiss()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()

    }

}