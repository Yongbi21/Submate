package com.example.chatmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.chatmessenger.activities.SearchUserActivity
import com.example.chatmessenger.activities.SettingsActivity
import com.example.chatmessenger.activities.SignInActivity
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.helper.TextToSpeechApiHandler
import com.example.chatmessenger.utility.VibrationUtil
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController


    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var token: String = ""

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navBar: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        drawerLayout = findViewById(R.id.drawerLayout)
        navBar = findViewById(R.id.navBar)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        generateToken()
        setDrawerLayoutNavBar(toolbar)

        val searchUserBtn = findViewById<ImageView>(R.id.searchUserBtn)

        searchUserBtn.setOnClickListener {

             VibrationUtil.vibrate(applicationContext)
            startActivity(Intent(applicationContext, SearchUserActivity::class.java))
        }

        val menu = navBar.menu

        val settingsItem = menu.findItem(R.id.settings)
        settingsItem.title = "Settings"
        settingsItem.actionView?.findViewById<TextView>(android.R.id.text1)?.apply {
            textSize = FontSizeHelper.getFontTitleSize(applicationContext)
        }

        val shareItem = menu.findItem(R.id.share)
        shareItem.title = "Share"
        shareItem.actionView?.findViewById<TextView>(android.R.id.text1)?.apply {
            textSize = FontSizeHelper.getFontTitleSize(applicationContext)
        }

        val logoutItem = menu.findItem(R.id.logout)
        logoutItem.title = "Logout"
        logoutItem.actionView?.findViewById<TextView>(android.R.id.text1)?.apply {
            textSize = FontSizeHelper.getFontTitleSize(applicationContext)
        }

    }

    public fun hideToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.visibility = View.INVISIBLE
        val searchUserBtn = findViewById<CardView>(R.id.servicesView)
        searchUserBtn.visibility = View.GONE
    }

    public fun showToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        Handler().postDelayed({
            toolbar.visibility = View.VISIBLE
            val searchUserBtn = findViewById<CardView>(R.id.servicesView)
            searchUserBtn.visibility = View.VISIBLE
        }, 330)
    }

    private fun setDrawerLayoutNavBar(toolbar: Toolbar) {
        title = ""
        val toggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navBar.setNavigationItemSelectedListener(this)
    }


    fun generateToken() {


        val firebaseInstance = FirebaseInstallations.getInstance()

        firebaseInstance.id.addOnSuccessListener { installationid ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener { gettocken ->

                token = gettocken


                val hasHamp = hashMapOf<String, Any>("token" to token)


                firestore.collection("Tokens").document(Utils.getUidLoggedIn()).set(hasHamp)
                    .addOnSuccessListener {


                    }


            }


        }.addOnFailureListener {


        }


    }


    override fun onResume() {
        super.onResume()

        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Online")


        }
    }

    override fun onPause() {
        super.onPause()


        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")


        }
    }

    override fun onDestroy() {
        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")


        }
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Online")


        }
    }



    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            // If we are on the Home fragment, exit the app
            if (navController.currentDestination?.id == R.id.homeFragment) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                if (auth.currentUser != null) {


                    firestore.collection("Users").document(Utils.getUidLoggedIn())
                        .update("status", "Offline")


                }
                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signOut()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }

            R.id.settings -> {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }

            R.id.share -> {
                shareApp()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        val appLink =
            "https://drive.google.com/file/d/129H9WVJtugyU_hUlhu9PG-owhPTkdWyH/view?usp=sharing"
        val message = "Check out this Submate app:\n$appLink"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

}







