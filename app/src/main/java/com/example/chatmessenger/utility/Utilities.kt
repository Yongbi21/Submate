package com.example.chatmessenger.utility

import java.util.Random

class Utilities {
    // Function to generate codes randomly to be send in gmail
    companion object {
        fun generateRandomCode(): String {
            val random = Random()
            val code: Int = 100000 + random.nextInt(900000)
            return code.toString()
        }
    }
}
