package com.example.chatmessenger.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.style.ImageSpan
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.adapter.MessageAdapter
import com.example.chatmessenger.databinding.DialogAddUserBinding
import com.example.chatmessenger.databinding.DialogDeleteUserBinding
import com.example.chatmessenger.databinding.FragmentChatBinding
import com.example.chatmessenger.databinding.LayoutSearchUserBinding
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.mvvm.ChatAppViewModel
import com.example.chatmessenger.fragments.ChatFragmentArgs
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.helper.TextToSpeechApiHandler
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Locale


class ChatFragment : Fragment(), MessageAdapter.MessageClickListener {

    lateinit var args: ChatFragmentArgs
    lateinit var binding: FragmentChatBinding
    lateinit var viewModel: ChatAppViewModel
    lateinit var adapter: MessageAdapter
    lateinit var toolbar: Toolbar
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var edMessage: EditText
    private lateinit var textViewStatus: TextView

    // Speech-to-text result handling
    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (results != null && results.isNotEmpty()) {
                    // Set the transcribed text into the EditText
                    edMessage.setText(results[0])
                    edMessage.setSelection(edMessage.text.length)
                    edMessage.requestFocus()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        binding.addButton.setOnClickListener {
            VibrationUtil.vibrate(requireContext())


            showDialogAdd(requireContext(), args.users, "Add")
        }
        binding.deleteButton.setOnClickListener {
            VibrationUtil.vibrate(requireContext())


            showDialogAdd(requireContext(), args.users, "Remove")
        }
        return binding.root
    }

    private fun onChangeStatus(otherUserId: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val myPath = firebaseUser?.uid
        val generalPath = "Add_Info"
        val query = FirebaseFirestore.getInstance()
            .collection(generalPath)
            .document(myPath.toString())
            .collection("friend")
            .whereEqualTo("otheruserid", otherUserId)

        query.get().addOnCompleteListener { myTask ->
            if (myTask.isSuccessful) {
                val myFriendshipDocs = myTask.result?.documents
                if (!myFriendshipDocs.isNullOrEmpty()) {
                    binding.deleteButton.visibility = View.VISIBLE
                    binding.addButton.visibility = View.GONE
                } else {
                    binding.deleteButton.visibility = View.GONE
                    binding.addButton.visibility = View.VISIBLE
                }
            } else {
                binding.deleteButton.visibility = View.GONE
                binding.addButton.visibility = View.GONE
            }
        }
    }

    private fun showDialogAdd(
        context: Context,
        user: Users,
        status: String,
    ) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()
        val generalPath = "Add_Info"
        val myPath = firebaseUser?.uid
        val otherPath = user.userid

        val myDataHashMap = hashMapOf(
            "otheruserid" to user.userid,
            "myuserid" to firebaseUser?.uid,
            "status" to "pending",
        )

        val otherDataHashMap = hashMapOf(
            "otheruserid" to firebaseUser?.uid,
            "myuserid" to user.userid,
            "status" to "pending",
        )

        if (status == "Add") {
            val dialog = Dialog(context)
            val viewBinding = DialogAddUserBinding.inflate(LayoutInflater.from(context))

            viewBinding.messageText.textSize = FontSizeHelper.getFontDescriptionSize(context)
            FontFamilyHelper.applyFontFamily(context, viewBinding.messageText)

            viewBinding.cancelButton.setOnClickListener {
                VibrationUtil.vibrate(requireContext())

                dialog.dismiss()
            }
            viewBinding.yesButton.setOnClickListener {
                VibrationUtil.vibrate(requireContext())


                dialog.dismiss()
                if (myPath != null && otherPath != null) {
                    firestore.collection(generalPath).document(myPath).collection("friend")
                        .add(myDataHashMap)
                    firestore.collection(generalPath).document(otherPath).collection("friend")
                        .add(otherDataHashMap)
                }

                binding.addButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
                Toast.makeText(context, "Added user succeed!", Toast.LENGTH_SHORT).show()
            }

            dialog.setCancelable(false)
            dialog.setContentView(viewBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        } else if (status == "Remove") {
            val dialog = Dialog(context)
            val viewBinding = DialogDeleteUserBinding.inflate(LayoutInflater.from(context))

            viewBinding.messageText.textSize = FontSizeHelper.getFontDescriptionSize(context)
            FontFamilyHelper.applyFontFamily(context, viewBinding.messageText)

            viewBinding.cancelButton.setOnClickListener {
                VibrationUtil.vibrate(requireContext())

                dialog.dismiss()
            }
            viewBinding.yesButton.setOnClickListener {
                VibrationUtil.vibrate(requireContext())


                dialog.dismiss()

                val collectionRef = FirebaseFirestore.getInstance()
                    .collection(generalPath)
                    .document(myPath.toString())
                    .collection("friend")

                val query = collectionRef.whereEqualTo("otheruserid", otherPath)

                query.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            collectionRef.document(document.id).delete()
                        }
                        binding.deleteButton.visibility = View.GONE
                        binding.addButton.visibility = View.VISIBLE
                        Toast.makeText(context, "Deleted user succeed!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Error deleting user: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            dialog.setCancelable(false)
            dialog.setContentView(viewBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        val chatBackBtn = toolbar.findViewById<ImageView>(R.id.chatBackBtn)
        textViewStatus = view.findViewById(R.id.chatUserStatus)

        textViewName.textSize = FontSizeHelper.getFontTitleSize(requireContext())
        textViewStatus.textSize = FontSizeHelper.getFontDescriptionSize(requireContext())


        FontFamilyHelper.applyFontFamily(requireContext(), textViewName)
        FontFamilyHelper.applyFontFamily(requireContext(), textViewStatus)

        args = ChatFragmentArgs.fromBundle(requireArguments())
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context)
            .load(args.users.imageUrl!!)
            .placeholder(R.drawable.person)
            .dontAnimate()
            .into(circleImageView)
        textViewName.text = args.users.username
        textViewStatus.text = args.users.status

        args.users.userid?.let { onChangeStatus(it) }

        chatBackBtn.setOnClickListener {
            VibrationUtil.vibrate(requireContext())


            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }

        binding.sendBtn.setOnClickListener {
            VibrationUtil.vibrate(requireContext())

            var message = edMessage.text.toString()
            if (message.isEmpty()){
                Toast.makeText(requireContext(), "Please type something...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.users.userid!!,
                args.users.username!!,
                args.users.imageUrl!!
            )
        }
//
//        val settingsIcon = toolbar.findViewById<ImageView>(R.id.fontButton)
//        settingsIcon.setOnClickListener {VibrationUtil.vibrate(applicationContext)


//            view.findNavController().navigate(R.id.action_chatFragment_to_fontSettingFragment)
//        }

        // Initialize text-to-speech
        textToSpeech = TextToSpeech(view.context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(
                        view.context,
                        "language is not supported",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Initialize speech-to-text
        val speechButton = view.findViewById<ImageButton>(R.id.speechButton)
        edMessage = view.findViewById(R.id.editTextMessage)
        speechButton.setOnClickListener {
            VibrationUtil.vibrate(requireContext())


            textToSpeech.stop()
            edMessage.setText(null)
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Speak Your Mind")
                result.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Correcting the casting for voiceButton
        val voiceButton = view.findViewById<View>(R.id.voiceButton)
        voiceButton.setOnClickListener {
            VibrationUtil.vibrate(requireContext())


            view.findNavController().navigate(R.id.action_chatFragment_to_callFragment)
        }



        viewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            val mainActivityInstance = activity as MainActivity
            mainActivityInstance.hideToolbar()
        }
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter(this)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        binding.messagesRecyclerView.layoutManager = layoutManager



        adapter.setList(list)
        binding.messagesRecyclerView.adapter = adapter

        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    val position = layoutManager.findFirstVisibleItemPosition()
                    if (position != RecyclerView.NO_POSITION && position < list.size) {
                        val clickedMessage = list[position]


                        val apiHandler = TextToSpeechApiHandler()

                        apiHandler.synthesizeTextToSpeech(
                            clickedMessage.message ?: "",
                            "fil-PH-1"
                        ) { audioUrl ->
                            if (audioUrl != null) {
                                apiHandler.playAudioFromUrl(audioUrl)
                            } else {
                                // Handle error
                                textToSpeech.speak(
                                    clickedMessage.message ?: "",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null
                                )
                            }
                        }
                        apiHandler.releaseMediaPlayer()
                    }

                    return true
                }
            })

        binding.messagesRecyclerView.addOnItemTouchListener(object :
            RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    return true
                }
                return false
            }
        })
    }

    override fun onMessageClick(message: String?) {
        if (!message.isNullOrBlank()) {
            // Play the message using the TextToSpeechApiHandler
            val apiHandler = TextToSpeechApiHandler()
            apiHandler.synthesizeTextToSpeech(message, "fil-PH-1") { audioUrl ->
                if (audioUrl != null) {
                    apiHandler.playAudioFromUrl(audioUrl)
                } else {
                    // Handle error by using the default TextToSpeech
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "tagalog_utterance")
                }
            }
            // Release the resources used by the TextToSpeechApiHandler
            apiHandler.releaseMediaPlayer()
        }
    }

}
