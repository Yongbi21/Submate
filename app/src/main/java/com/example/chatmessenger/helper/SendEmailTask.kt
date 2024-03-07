package com.example.chatmessenger.helper

import android.os.AsyncTask


/**
 * AsyncTask for sending an email.
 *
 * @param toEmail The recipient email address.
 * @param verificationCode The verification code to be sent.
 */


class SendEmailTask(private val toEmail: String, private val verificationCode: String) :
    AsyncTask<Void?, Void?, Void?>() {

    /**
     * Background task to send the email.
     */

    override fun doInBackground(vararg params: Void?): Void? {
        val emailSender = GmailSender()
        emailSender.sendEmail(verificationCode, toEmail)
        return null
    }

    /**
     * Callback method invoked after the task is completed.
     */

    override fun onPostExecute(result: Void?) {}
}