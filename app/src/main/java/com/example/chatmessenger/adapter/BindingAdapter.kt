package com.example.chatmessenger.databinding

import android.util.TypedValue
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("fontSize")
    fun TextView.setFontSize(fontSize: Float) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    }
}