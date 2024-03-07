package com.example.chatmessenger.databinding

import android.util.TypedValue
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 *
 * This object contains custom binding adapters for data binding in Android.
 */

object BindingAdapters {

    /**
     * Binding adapter for setting font size of a TextView using data binding.
     * @param fontSize The font size value in sp (scaled pixels).
     */

    @JvmStatic
    @BindingAdapter("fontSize")
    fun TextView.setFontSize(fontSize: Float) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    }
}