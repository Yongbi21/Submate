@file:Suppress("DEPRECATION")

package com.example.chatmessenger.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.activities.SignInActivity
import com.example.chatmessenger.adapter.*
import com.example.chatmessenger.databinding.FragmentHomeBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.modal.RecentChats
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.mvvm.ChatAppViewModel
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Fragment responsible for managing the home screen UI.
 * Displays a list of users and recent chats.
 * Allows users to navigate to the settings screen.
 * Provides options to log out from the application.
 *
 * @property rvUsers RecyclerView for displaying the list of users.
 * @property rvRecentChats RecyclerView for displaying the list of recent chats.
 * @property adapter Adapter for the user list RecyclerView.
 * @property viewModel ViewModel responsible for managing UI data and business logic.
 * @property toolbar Toolbar for the home screen.
 * @property circleImageView CircleImageView for displaying the user profile image in the toolbar.
 * @property recentadapter Adapter for the recent chats RecyclerView.
 * @property firestore Instance of FirebaseFirestore for accessing Firestore database.
 * @property binding The data binding instance for the fragment's layout.
 */


class HomeFragment : Fragment(), OnItemClickListener, onChatClicked {


    lateinit var rvUsers: RecyclerView
    lateinit var rvRecentChats: RecyclerView
    lateinit var adapter: UserAdapter
    lateinit var viewModel: ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentadapter: RecentChatAdapter
    lateinit var firestore: FirebaseFirestore
    lateinit var binding: FragmentHomeBinding


    private fun refreshTextSize() {
        binding.titleText.textSize = FontSizeHelper.getFontTitleSize(requireContext())
        FontFamilyHelper.applyFontFamily(requireContext(), binding.titleText)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        refreshTextSize()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        toolbar = view.findViewById(R.id.toolbarMain)
        val logoutimage = toolbar.findViewById<ImageView>(R.id.logOut)
        circleImageView = toolbar.findViewById(R.id.tlImage)



        binding.lifecycleOwner = viewLifecycleOwner



        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {


            Glide.with(requireContext()).load(it).into(circleImageView)


        })



        firestore = FirebaseFirestore.getInstance()


        val firebaseAuth = FirebaseAuth.getInstance()



        logoutimage.setOnClickListener {
            VibrationUtil.vibrate(requireContext())




            firebaseAuth.signOut()

            startActivity(Intent(requireContext(), SignInActivity::class.java))


        }


        rvUsers = view.findViewById(R.id.rvUsers)
        rvRecentChats = view.findViewById(R.id.rvRecentChats)
        adapter = UserAdapter()
        recentadapter = RecentChatAdapter()


        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(activity)

        rvUsers.layoutManager = layoutManager
        rvRecentChats.layoutManager = layoutManager2


        viewModel.getUsers().observe(viewLifecycleOwner, Observer {

            adapter.setList(it)
            rvUsers.adapter = adapter


        })


        circleImageView.setOnClickListener {
            VibrationUtil.vibrate(requireContext())




            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)


        }


        adapter.setOnClickListener(this)





        viewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {


            recentadapter.setList(it)
            rvRecentChats.adapter = recentadapter


        })

        recentadapter.setOnChatClickListener(this)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            val mainActivityInstance = activity as MainActivity
            mainActivityInstance.showToolbar()
        }
        refreshTextSize()
        recentadapter.notifyDataSetChanged()
    }


    override fun onUserSelected(position: Int, users: Users) {

        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)


    }


    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {


        val action = HomeFragmentDirections.actionHomeFragmentToChatfromHome(chatList)
        view?.findNavController()?.navigate(action)


    }


}