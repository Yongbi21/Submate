@file:Suppress("DEPRECATION")

package com.example.chatmessenger.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.databinding.FragmentSettingBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.mvvm.ChatAppViewModel
import com.example.chatmessenger.utility.VibrationUtil
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * Fragment for managing user settings.
 * Allows users to update their profile information and profile picture.
 * Users can select their gender, update their profile, and change their profile picture either by taking a photo with the camera or choosing from the gallery.
 * Handles image uploading to Firebase Storage.
 * Uses ViewModel to observe and update profile information.
 * Utilizes custom FontSizeHelper and FontFamilyHelper to manage text size and font family.
 *
 * @property viewModel The ViewModel responsible for managing UI data and business logic.
 * @property binding The data binding instance for the fragment's layout.
 * @property storageRef Reference to Firebase Storage for managing image uploads.
 * @property storage Instance of FirebaseStorage.
 * @property uri The URI of the selected image.
 * @property bitmap The bitmap representation of the selected image.
 * @property gender The selected gender of the user.
 */


class SettingFragment : Fragment() {

    lateinit var viewModel: ChatAppViewModel
    lateinit var binding: FragmentSettingBinding

    private lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage
    var uri: Uri? = null

    lateinit var bitmap: Bitmap
    private var gender: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        refreshTextSize()



        return binding.root
    }

    private fun refreshTextSize() {
        binding.titleText.textSize = FontSizeHelper.getFontTitleSize(requireContext())
        binding.firstnameText.textSize = FontSizeHelper.getFontDescriptionSize(requireContext())
        binding.lastnameText.textSize = FontSizeHelper.getFontDescriptionSize(requireContext())

        FontFamilyHelper.applyFontFamily(requireContext(), binding.titleText)
        FontFamilyHelper.applyFontFamily(requireContext(), binding.firstnameText)
        FontFamilyHelper.applyFontFamily(requireContext(), binding.lastnameText)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        val genderArray = resources.getStringArray(R.array.gender)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        viewModel.gender.observe(viewLifecycleOwner) { genderValue ->
            gender = genderValue
            binding.spinnerGender.setSelection(genderArray.indexOf(gender))
        }


        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {


            loadImage(it)


        })

        //Get Gender List
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        binding.spinnerGender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                gender = adapterView.getItemAtPosition(i).toString()
                viewModel.gender.value = gender
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.settingBackBtn.setOnClickListener {
            VibrationUtil.vibrate(requireContext())



            view.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)


        }

        binding.settingUpdateButton.setOnClickListener {
            VibrationUtil.vibrate(requireContext())

            if (gender == "Select Gender" || gender.isEmpty()){
                Toast.makeText(requireContext(), "Please select gender!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            viewModel.updateProfile()


        }


        binding.settingUpdateImage.setOnClickListener {
            VibrationUtil.vibrate(requireContext())


            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {

                        takePhotoWithCamera()


                    }

                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
                    }

                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()


        }


    }


    private fun loadImage(imageUrl: String) {


        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.person).dontAnimate()
            .into(binding.settingUpdateImage)


    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {

        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    // To take a photo with the camera, you can use this code
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)


    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    uploadImageToFirebaseStorage(imageBitmap)
                }

                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }


    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {

        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        bitmap = imageBitmap!!

        binding.settingUpdateImage.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {


            val task = it.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it
                viewModel.imageUrl.value = uri.toString()


            }






            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()

        refreshTextSize()
        if (activity is MainActivity) {
            val mainActivityInstance = activity as MainActivity
            mainActivityInstance.hideToolbar()
        }

        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {


            loadImage(it)


        })


    }


}