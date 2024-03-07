package com.example.chatmessenger.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chatmessenger.R

class AudioCallFragment : Fragment() {

    private lateinit var profileBackground: ImageView
    private lateinit var endCallButton: Button
    private lateinit var subtitlesButton: Button
    private lateinit var muteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.callactivity, container, false)

        // Initialize views
        profileBackground = view.findViewById(R.id.profileBackground)
        endCallButton = view.findViewById(R.id.endCallButton)
        subtitlesButton = view.findViewById(R.id.subtitlesButton)
        muteButton = view.findViewById(R.id.muteButton)

        // Set click listeners and handle logic for each button
        // ...

        val userProfileBackgroundImage = getUserProfileBackgroundImage()
        profileBackground.setImageDrawable(userProfileBackgroundImage)

        return view
    }

    private fun getUserProfileBackgroundImage(): Drawable? {
        // Replace R.drawable.userProfileImage with your actual image resource
        return ContextCompat.getDrawable(requireContext(), R.drawable.estetik)
    }


}
