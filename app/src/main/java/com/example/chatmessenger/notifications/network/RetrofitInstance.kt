package com.example.chatmessenger.notifications.network

import com.example.chatmessenger.notifications.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// This class handles the creation of a Retrofit instance for network communication


class RetrofitInstance {

    companion object {

        // Lazily initializes a Retrofit instance with the base URL and Gson converter

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // Lazily initializes the API service using the Retrofit instance

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}