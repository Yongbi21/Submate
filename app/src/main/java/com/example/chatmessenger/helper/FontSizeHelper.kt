package com.example.chatmessenger.helper

import android.content.Context
import android.content.SharedPreferences

class FontSizeHelper(context: Context) {

    companion object {
        private const val PREFS_NAME = "FontSizePrefs"
        private const val FONT_SIZE_KEY = "fontSizeKey"

        private val DEFAULT_FONT_SIZE = 24f
        private val DEFAULT_FONT_SIZE_PROGRESS = 1

        private val FONT_SIZE_MAP = mapOf(
            0 to 20f,
            1 to 24f,
            2 to 28f,
            3 to 31f,
            4 to 34f
        )

        @JvmStatic
        fun getFontTitleSize(context: Context): Float {
            var progress = FontSizeHelper(context).getFontSizeProgress()
            return FONT_SIZE_MAP[progress] ?: DEFAULT_FONT_SIZE
        }

        @JvmStatic
        fun calcualteFontTitleSizeWithProgress(progress: Int): Float {
            return FONT_SIZE_MAP[progress] ?: DEFAULT_FONT_SIZE
        }

        @JvmStatic
        fun getFontDescriptionSize(context: Context): Float {
            return getFontTitleSize(context) - 5
        }

        @JvmStatic
        fun getFontSmallDescriptionSize(context: Context): Float {
            return getFontTitleSize(context) - 11
        }

        @JvmStatic
        fun getFontSmallIndicatorSize(context: Context): Float {
            var size = getFontTitleSize(context) - 10
            if (size >= 18.0f) {
                return 18.0f;
            }
            return if (size <= 10.0f) 10.0f else size
        }

        @JvmStatic
        fun getFontLoginSmallDescriptionSize(context: Context): Float {
            return getFontTitleSize(context) - 10
        }

        @JvmStatic
        fun getFontRecentMessageDescriptioSize(context: Context): Float {
            return getFontTitleSize(context) - 9
        }


        @JvmStatic
        fun getFontRecentTitleDescriptioSize(context: Context): Float {
            return getFontTitleSize(context) - 7
        }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveFontSizeProgress(progress: Int) {
        sharedPreferences.edit().putInt(FONT_SIZE_KEY, progress).apply()
    }

    fun getFontSizeProgress(): Int {
        return sharedPreferences.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE_PROGRESS)
    }

    fun saveFontSize(fontSize: Float) {
        val progress = FONT_SIZE_MAP.entries.find { it.value == fontSize }?.key
            ?: DEFAULT_FONT_SIZE_PROGRESS
        saveFontSizeProgress(progress)
    }

    fun getFontSize(): Float {
        val progress = getFontSizeProgress()
        return calcualteFontTitleSizeWithProgress(progress)
    }
}
