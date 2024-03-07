package com.example.chatmessenger

import android.app.Application

class MyApplication : Application() {

    // Companion object allows access to properties and methods without needing an instance of the class.
    companion object {
        // 'instance' will hold the singleton instance of MyApplication.
        lateinit var instance: MyApplication
        const val activityResultOk = android.app.Activity.RESULT_OK
    }

    // This method is called when the application is starting up.
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
