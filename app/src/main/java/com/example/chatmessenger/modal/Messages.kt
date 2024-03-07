package com.example.chatmessenger.modal


/**
 * Data class representing a message exchanged between users.
 *
 * @property sender The ID of the sender of the message.
 * @property receiver The ID of the receiver of the message.
 * @property message The content of the message.
 * @property time The timestamp of when the message was sent.
 * @property fontSize The font size of the message (default is "medium").
 * @property id A unique identifier for the message.
 */

data class Messages(
    val sender : String? = "",
    val receiver: String? = "",
    val message: String? = "",
    val time: String? = "",
    val fontSize: String = "medium"
) {

    val id : String get() = "$sender-$receiver-$message-$time"
}