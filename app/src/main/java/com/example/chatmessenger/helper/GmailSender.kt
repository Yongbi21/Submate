package com.example.chatmessenger.helper
import javax.mail.Transport

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Class responsible for sending emails using Gmail SMTP server.
 */

class GmailSender {
    private val session: Session

    // Configure the SMTP server and other settings
    init {
        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    "genshinemaildummy@gmail.com",
                    "yrfr qofn adst mcwp"
                )
            }
        })
    }

    /**
     * Sends an email with the provided verification code to the specified email address.
     *
     * @param codeSend The verification code to be sent.
     * @param email The recipient's email address.
     */


    fun sendEmail(
        codeSend: String,
        email: String
    ) {
        if (codeSend.isEmpty()) {
            return
        }

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress("genshinemaildummy@gmail.com"))
            message.addRecipient(
                Message.RecipientType.TO,
                InternetAddress(email)
            )
            message.setSubject("Your Chat-Messenger Verification Code")
            message.setText("Thank you for signing up with Chat-Messenger! To complete your account setup and ensure the security of your information, please enter the following verification code:\n" +
                    "\n" +
                    "Verification Code: "+ codeSend + "\n" +
                    "\n" +
                    "This code is valid anytime and is required to verify your account. Please do not share this code with anyone.\n" +
                    "\n" +
                    "If you did not initiate this request, please ignore this email or contact our support team immediately at genshinemaildummy@gmail.com to ensure your account's security.\n" +
                    "\n" +
                    "Thank you for choosing Chat-Messenger. We're excited to have you on board!\n" +
                    "\n" +
                    "Warm regards,\n" +
                    "The Chat-Messenger Team"+ codeSend)
            Transport.send(message)
        } catch (ex: MessagingException) {
        }
    }

}

