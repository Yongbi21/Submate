package com.example.chatmessenger.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.adapter.CustomSpinnerAdapter
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.databinding.ActivitySettingsBinding
import com.example.chatmessenger.databinding.DialogSaveFontsizeBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.utility.VibrationUtil

/**
 * Activity for user settings, including font size and family customization.
 */

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    var currentProgress = 0
    var currentFontFamily = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            VibrationUtil.vibrate(applicationContext)

             finish() }

        currentFontFamily = FontFamilyHelper(applicationContext).getSavedFontFamily().toString()

        // Set up the Spinner with font size options
        val fontSizeAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.font_family_options,
            R.layout.spinner_dropdown_item
        )

        val fontFamilies = resources.getStringArray(R.array.font_family_options)
        val adapter = CustomSpinnerAdapter(this, fontFamilies)

        binding.fontSizeSpinner.adapter = adapter
        binding.fontSizeSpinner.setSelection(
            fontSizeAdapter.getPosition(
                currentFontFamily
            )
        )

        binding.fontSizeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedFontSize = parentView?.getItemAtPosition(position).toString()
                    currentFontFamily = selectedFontSize
                    FontFamilyHelper.setFontFamilySetting(binding.titleText, currentFontFamily)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                }
            }

        currentProgress = FontSizeHelper(applicationContext).getFontSizeProgress()
        binding.fontSizeSeekBar.progress = currentProgress
        refreshTextSize()

        binding.fontSizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentProgress = progress
                val fontSize = FontSizeHelper.calcualteFontTitleSizeWithProgress(progress)
                binding.titleText.textSize = fontSize
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.fontSaveButton.setOnClickListener {VibrationUtil.vibrate(applicationContext)

            
            val dialog = Dialog(this)
            val viewBinding = DialogSaveFontsizeBinding.inflate(layoutInflater)


            viewBinding.messageText.textSize = FontSizeHelper.getFontDescriptionSize(this)
            FontFamilyHelper.applyFontFamily(this, viewBinding.messageText)

            viewBinding.cancelButton.setOnClickListener {VibrationUtil.vibrate(applicationContext)

             dialog.dismiss() }
            viewBinding.yesButton.setOnClickListener {VibrationUtil.vibrate(applicationContext)

            
                dialog.dismiss()
                FontSizeHelper(applicationContext).saveFontSizeProgress(currentProgress)
                FontFamilyHelper(applicationContext).saveFontFamily(currentFontFamily)
                Toast.makeText(this, "Saved changes succeed!", Toast.LENGTH_SHORT).show()
            }

            dialog.setCancelable(false)
            dialog.setContentView(viewBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }


    override fun onResume() {
        super.onResume()
    }

    /**
     * Refresh text size and font family for all relevant views.
     */

    private fun refreshTextSize() {
        binding.titleText.textSize = FontSizeHelper.getFontTitleSize(applicationContext)
        binding.toolbarTitleText.textSize = FontSizeHelper.getFontTitleSize(applicationContext)
        binding.text1.textSize = FontSizeHelper.getFontSmallIndicatorSize(applicationContext)
        binding.text2.textSize = FontSizeHelper.getFontSmallIndicatorSize(applicationContext)
        binding.text3.textSize = FontSizeHelper.getFontSmallIndicatorSize(applicationContext)
        binding.text4.textSize = FontSizeHelper.getFontSmallIndicatorSize(applicationContext)
        binding.text5.textSize = FontSizeHelper.getFontSmallIndicatorSize(applicationContext)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.toolbarTitleText)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.titleText)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.text1)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.text2)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.text3)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.text4)
        FontFamilyHelper.applyFontFamily(applicationContext, binding.text5)
    }
}
