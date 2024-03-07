package com.example.chatmessenger.fragments

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import com.example.chatmessenger.fragments.ChatfromHomeArgs
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.activities.SendCodeActivity
import com.example.chatmessenger.adapter.MessageAdapter
import com.example.chatmessenger.databinding.FragmentChatfromHomeBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.helper.TextToSpeechApiHandler
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.mvvm.ChatAppViewModel
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Locale

class ChatfromHome : Fragment(), MessageAdapter.MessageClickListener {

    lateinit var args: ChatfromHomeArgs
    lateinit var binding: FragmentChatfromHomeBinding
    lateinit var viewModel: ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var adapter: MessageAdapter
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var edMessage: EditText

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (results != null && results.isNotEmpty()) {
                    edMessage?.setText(results[0])
                    edMessage.setSelection(edMessage.text.length)
                    edMessage.requestFocus()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            val mainActivityInstance = activity as MainActivity
            mainActivityInstance.hideToolbar()
        }
    }

    private lateinit var auth: FirebaseAuth
    val firestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        val textViewStatus = view.findViewById<TextView>(R.id.chatUserStatus)

        args = ChatfromHomeArgs.fromBundle(requireArguments())
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context).load(args.recentchats.friendsimage!!)
            .placeholder(R.drawable.person).dontAnimate().into(circleImageView)
        textViewName.text = args.recentchats.name

        textViewName.textSize = FontSizeHelper.getFontTitleSize(requireContext())
        textViewStatus.textSize = FontSizeHelper.getFontDescriptionSize(requireContext())

        FontFamilyHelper.applyFontFamily(requireContext(), textViewName)
        FontFamilyHelper.applyFontFamily(requireContext(), textViewStatus)

        if (auth.currentUser != null) {
            var userID = args.recentchats.friendid
            if (userID != null) {
                firestore.collection("Users").document(userID)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val value = task.result
                            if (value != null && value.exists()) {
                                val users = value.toObject(Users::class.java)
                                val status = users?.status ?: ""
                                textViewStatus.text = status
                            }
                        } else {

                        }
                    }
            }
        }

        binding.chatBackBtn.setOnClickListener {
            VibrationUtil.vibrate(requireContext())
            view.findNavController().navigate(R.id.action_chatfromHome_to_homeFragment)
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
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!
            )
        }

        viewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })

        textToSpeech = TextToSpeech(view.context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(view.context, "language is not supported", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        val speechButton = view.findViewById<ImageButton>(R.id.SpeechButton2)
        edMessage = view.findViewById(R.id.editTextMessage2)

        speechButton.setOnClickListener {
            VibrationUtil.vibrate(requireContext())
            textToSpeech.stop()
            edMessage?.setText(null)
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Speak Your Mind")
                result.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter(this)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        binding.messagesRecyclerView.layoutManager = layoutManager
        adapter.setList(list)
        binding.messagesRecyclerView.adapter = adapter

        // Set up tap gesture detection for the TextView in each message item
        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    // Handle tap event
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

        // Attach the gesture detector to the TextView in each message item
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

    //Text To Speech

    override fun onMessageClick(message: String?) {
        if (!message.isNullOrBlank()) {
            val apiHandler = TextToSpeechApiHandler()

            apiHandler.synthesizeTextToSpeech(message, "fil-PH-1") { audioUrl ->
                if (audioUrl != null) {
                    apiHandler.playAudioFromUrl(audioUrl)
                } else {
                    // Handle error
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            apiHandler.releaseMediaPlayer()
        }
    }


}
