package com.example.chatmessenger.helper

import android.os.AsyncTask

class SendEmailTask(private val toEmail: String, private val verificationCode: String) :
    AsyncTask<Void?, Void?, Void?>() {
    override fun doInBackground(vararg params: Void?): Void? {
        val emailSender = GmailSender()
        emailSender.sendEmail(verificationCode, toEmail)
        return null
    }

    override fun onPostExecute(result: Void?) {}
}