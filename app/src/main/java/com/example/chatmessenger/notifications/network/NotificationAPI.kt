package com.example.chatmessenger.notifications.network

import com.example.chatmessenger.notifications.Constants.Companion.CONTENT_TYPE
import com.example.chatmessenger.notifications.Constants.Companion.SERVER_KEY
import com.example.chatmessenger.notifications.entity.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Retrofit interface for handling notification API requests
// Define headers for the API request
// Define a POST request to send push notifications

interface NotificationAPI {
    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")

    // Suspend function to asynchronously send a push notification

    suspend fun postNotification(

        // Request body containing the push notification data

        @Body notification: PushNotification
    ): Response<ResponseBody> // Response containing the result of the API call
}
