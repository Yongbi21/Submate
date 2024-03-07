package com.example.chatmessenger.helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.chatmessenger.R

class FontFamilyHelper(context: Context) {

    companion object {
        private const val PREFS_NAME = "FontFamilyPrefs"
        private const val FONT_FAMILY_KEY = "fontFamilyKey"
        private const val DEFAULT_FONT_FAMILY = "nunito"

        @JvmStatic
        fun applyFontFamily(context: Context, textView: TextView) {
            val fontFamily = FontFamilyHelper(context).getSavedFontFamily() ?: DEFAULT_FONT_FAMILY
            setFontFamily(textView, fontFamily)
        }

        public fun setFontFamilySetting(textView: TextView, fontFamily: String) {
            val typeface = getFontFamily(textView.context, fontFamily, textView.typeface?.style ?: Typeface.NORMAL)
            textView.typeface = typeface
        }

        private fun setFontFamily(textView: TextView, fontFamily: String) {
            val typeface = getFontFamily(textView.context, fontFamily, textView.typeface?.style ?: Typeface.NORMAL)
            textView.typeface = typeface
        }

        private fun getFontFamily(context: Context, fontFamily: String, style: Int): Typeface {
            return try {
                val resId = context.resources.getIdentifier(fontFamily, "font", context.packageName)
                val baseTypeface = ResourcesCompat.getFont(context, resId)
                Typeface.create(baseTypeface, style)
            } catch (e: Exception) {
                Typeface.DEFAULT
            }
        }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveFontFamily(fontFamily: String) {
        sharedPreferences.edit().putString(FONT_FAMILY_KEY, fontFamily).apply()
    }

    fun getSavedFontFamily(): String? {
        return sharedPreferences.getString(FONT_FAMILY_KEY, "nunito")
    }
}
