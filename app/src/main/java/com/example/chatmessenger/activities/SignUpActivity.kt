@file:Suppress("DEPRECATION")

package com.example.chatmessenger.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chatmessenger.R
import com.example.chatmessenger.databinding.ActivitySignUpBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.helper.SendEmailTask
import com.example.chatmessenger.utility.Utilities
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class  SignUpActivity : AppCompatActivity() {

    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private lateinit var signupAuth: FirebaseAuth
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmpassword: String
    private var gender: String = ""

    private fun refreshTextSize() {
        signUpBinding.firstnameText.textSize =
            FontSizeHelper.getFontDescriptionSize(applicationContext)
        signUpBinding.lastnameText.textSize =
            FontSizeHelper.getFontDescriptionSize(applicationContext)
        signUpBinding.confirmPassword.textSize =
            FontSizeHelper.getFontDescriptionSize(applicationContext)
        signUpBinding.signUpEmail.textSize =
            FontSizeHelper.getFontDescriptionSize(applicationContext)
        signUpBinding.signUpPassword.textSize =
            FontSizeHelper.getFontDescriptionSize(applicationContext)
        signUpBinding.SignDetails.textSize =
            FontSizeHelper.getFontLoginSmallDescriptionSize(applicationContext)
        signUpBinding.signUpTextToSignIn.textSize =
            FontSizeHelper.getFontLoginSmallDescriptionSize(applicationContext)
        signUpBinding.titleText.textSize = FontSizeHelper.getFontTitleSize(applicationContext)

        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.firstnameText)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.lastnameText)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.confirmPassword)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.signUpEmail)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.signUpPassword)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.SignDetails)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.signUpTextToSignIn)
        FontFamilyHelper.applyFontFamily(applicationContext, signUpBinding.titleText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        signupAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        progressDialog = ProgressDialog(this)


        signUpBinding.showpass.setOnClickListener {
            showAndHidePassword()
        }

        signUpBinding.signUpTextToSignIn.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)



            startActivity(Intent(this, SignInActivity::class.java))


        }

        refreshTextSize()

        signUpBinding.settingsButton.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)


            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        }


        //Get Gender List
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        signUpBinding.spinnerGender.adapter = adapter
        signUpBinding.spinnerGender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                gender = adapterView.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        signUpBinding.signUpBtn.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)




            firstname = signUpBinding.firstnameText.text.toString()
            lastname = signUpBinding.lastnameText.text.toString()
            email = signUpBinding.signUpEmail.text.toString()
            password = signUpBinding.signUpPassword.text.toString()
            confirmpassword = signUpBinding.confirmPassword.text.toString()


            if (signUpBinding.firstnameText.text.isEmpty()) {
                Toast.makeText(this, "Enter firstname", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (signUpBinding.lastnameText.text.isEmpty()) {
                Toast.makeText(this, "Enter lastname", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (gender == "Select Gender" || gender == "") {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (signUpBinding.signUpEmail.text.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (signUpBinding.signUpPassword.text.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (signUpBinding.signUpPassword.text.length <= 5) {
                Toast.makeText(this, "Password must atleast 6 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (signUpBinding.confirmPassword.text.isEmpty()) {
                Toast.makeText(this, "Enter confirm password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmpassword) {
                Toast.makeText(this, "Confirm password not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (signUpBinding.firstnameText.text.isNotEmpty() &&
                signUpBinding.signUpEmail.text.isNotEmpty() &&
                signUpBinding.signUpPassword.text.isNotEmpty() &&
                signUpBinding.lastnameText.text.isNotEmpty() &&
                signUpBinding.confirmPassword.text.isNotEmpty()
            ) {
                createAnAccount(firstname, lastname, gender, password, email)
            }


        }

    }

    private fun showAndHidePassword() {
        if (signUpBinding.showpass.isChecked) {
            signUpBinding.signUpPassword.transformationMethod = null
            signUpBinding.confirmPassword.transformationMethod = null
        } else {
            signUpBinding.signUpPassword.transformationMethod = PasswordTransformationMethod()
            signUpBinding.confirmPassword.transformationMethod = PasswordTransformationMethod()
        }
        signUpBinding.signUpPassword.setSelection(signUpBinding.signUpPassword.length())
        signUpBinding.confirmPassword.setSelection(signUpBinding.confirmPassword.length())
    }

    override fun onResume() {
        super.onResume()
        refreshTextSize()
    }

    private fun createAnAccount(
        firstname: String,
        lastname: String,
        gender: String,
        password: String,
        email: String
    ) {


        progressDialog.show()
        progressDialog.setMessage("Registering User")

        signupAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var verificationCode = Utilities.generateRandomCode()
                // User registration successful
                val user = signupAuth.currentUser
                val dataHashMap = hashMapOf(
                    "userid" to user?.uid,
                    "username" to "$firstname $lastname",
                    "useremail" to email,
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "status" to "Offline",
                    "gender" to gender,
                    "code" to verificationCode,
                    "verified" to "false",
                    "usernamelowercase" to "$firstname $lastname".lowercase(Locale.getDefault()),
                    "imageUrl" to "https://exoffender.org/wp-content/uploads/2016/09/empty-profile.png"
                )

                SendEmailTask(email, verificationCode).execute()

                firestore.collection("Users").document(user?.uid ?: "").set(dataHashMap)
                progressDialog.dismiss()
                startActivity(Intent(this, SendCodeActivity::class.java))
                finish()
            } else {
                // User registration failed, handle the error
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Registration failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }


}