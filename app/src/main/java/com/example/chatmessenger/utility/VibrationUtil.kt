package com.example.chatmessenger.utility
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object VibrationUtil {

    private const val DEFAULT_VIBRATION_DURATION = 100L // in milliseconds
    private const val DEFAULT_AMPLITUDE = 255

    fun vibrate(context: Context) {
        vibrate(context, DEFAULT_VIBRATION_DURATION)
    }

    private fun vibrate(context: Context, duration: Long) {
        vibrate(context, duration, DEFAULT_AMPLITUDE)
    }

    private fun vibrate(context: Context, duration: Long, amplitude: Int) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createOneShot(duration, amplitude)
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }
}

