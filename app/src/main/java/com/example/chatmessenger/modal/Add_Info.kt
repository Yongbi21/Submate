package com.example.chatmessenger.modal


/**
 * Data class representing additional information related to user connections.
 *
 * @property otheruserid The ID of the other user involved in the connection.
 * @property myuserid The ID of the current user involved in the connection.
 * @property status The status of the connection.
 */

class Add_Info(
    val otheruserid: String? = "",
    val myuserid: String? = "",
    val status: String? = "",
    )