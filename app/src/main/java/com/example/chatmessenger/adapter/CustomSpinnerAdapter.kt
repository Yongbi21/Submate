package com.example.chatmessenger.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.chatmessenger.R
import com.example.chatmessenger.helper.FontFamilyHelper

/**
 * Custom ArrayAdapter for a spinner, providing custom font family support.
 *
 * @param context The context in which the spinner adapter is used.
 * @param objects The array of items to be displayed in the spinner.
 *
 * Overrides the method to customize the appearance of the selected item view in the spinner.
 *
 * @param position The position of the item within the adapter's data set.
 * @param convertView The old view to reuse, if possible.
 * @param parent The parent view that this view will eventually be attached to.
 * @return The customized view representing the selected item.
 */

class CustomSpinnerAdapter(context: Context, objects: Array<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_dropdown_item, objects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val fontFamilies: Array<String> = context.resources.getStringArray(R.array.font_family_options)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        FontFamilyHelper.applyFontFamily(view.context, (view as TextView))

        return view
    }

    /**
     * Overrides the method to customize the appearance of dropdown items in the spinner.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will eventually be attached to.
     * @return The customized view representing a dropdown item.
     */

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.spinner_dropdown_item, parent, false)

//        (view as TextView).typeface = Typeface.createFromAsset(context.assets, getFontPath(position))
        val itemValue = getItem(position)
        Log.d("Baral", itemValue.toString()+"")
        FontFamilyHelper.setFontFamilySetting( (view as TextView), itemValue.toString())
        view.text = getItem(position)

        return view
    }

    private fun getFontPath(position: Int): String {
        // Ensure that position is within bounds
        val index = if (position >= fontFamilies.size) fontFamilies.size - 1 else position
        return "fonts/${fontFamilies[index]}.ttf" // Assuming your font files are in the "fonts" folder
    }
}
