package com.example.chatmessenger

import android.app.Application

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
        const val activityResultOk = android.app.Activity.RESULT_OK

    }


    override fun onCreate() {
        super.onCreate()

        instance = this
    }

}