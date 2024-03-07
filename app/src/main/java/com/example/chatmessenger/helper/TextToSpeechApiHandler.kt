package com.example.chatmessenger.helper

import android.media.MediaPlayer
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class TextToSpeechApiHandler {

    private val client = OkHttpClient()
    private var mediaPlayer: MediaPlayer? = null

    fun synthesizeTextToSpeech(text: String, voiceCode: String, callback: (String?) -> Unit) {
        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, "voice_code=$voiceCode&text=$text&speed=1&pitch=1")

        val request = Request.Builder()
            .url("https://cloudlabs-text-to-speech.p.rapidapi.com/synthesize")
            .post(requestBody)
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .addHeader("X-RapidAPI-Key", "874a42741cmshbceefe0abab00d9p1443f2jsnae317b20cc60")
            .addHeader("X-RapidAPI-Host", "cloudlabs-text-to-speech.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val jsonResponse = JSONObject(it.string())
                    val audioUrl = jsonResponse.optJSONObject("result")?.optString("audio_url")
                    callback(audioUrl)
                } ?: run {
                    // Handle response body null
                    callback(null)
                }
            }
        })
    }

    fun playAudioFromUrl(audioUrl: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        } else {
            mediaPlayer?.reset()
        }

        mediaPlayer?.setDataSource(audioUrl)
        mediaPlayer?.prepareAsync()

        mediaPlayer?.setOnPreparedListener {
            it.start()
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
