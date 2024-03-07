package com.example.chatmessenger.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatmessenger.R
import com.example.chatmessenger.databinding.DialogAddUserBinding
import com.example.chatmessenger.databinding.DialogDeleteUserBinding
import com.example.chatmessenger.databinding.DialogSaveFontsizeBinding
import com.example.chatmessenger.databinding.LayoutSearchUserBinding
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.modal.Add_Info
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.utility.VibrationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Adapter for the RecyclerView displaying a list of users in search results.
 * Responsible for inflating search user list item views and binding user data to them.
 *
 * @property searchUserList List of users to display in search results.
 */


class SearchUserAdapter(private val searchUserList: ArrayList<Users>) :
    RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {

    /**
     * ViewHolder for the search user item view.
     * Responsible for holding references to views within the item layout.
     *
     * @property binding The binding object for the search user item layout.
     */

    class ViewHolder(val binding: LayoutSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchUserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user = searchUserList[position]
        val binding = holder.binding
        binding.addButton.visibility = View.INVISIBLE
        binding.addButton.text = "-"

        binding.nameText.textSize =  FontSizeHelper.getFontRecentMessageDescriptioSize(holder.itemView.context)
        FontFamilyHelper.applyFontFamily(holder.itemView.context, binding.nameText)

        binding.nameText.text = user.username
        Glide.with(holder.itemView.context).load(user.imageUrl).into(binding.imageViewProfile)

        onChangeStatus(holder.itemView.context, binding, user)

        binding.addButton.setOnClickListener {
            VibrationUtil.vibrate(holder.itemView.context)

            
            showDialogAdd(holder.itemView.context, user, binding.addButton.text.toString(), binding)
        }
    }

    /**
     * Function to check and set status for add/remove button based on user's friend status.
     *
     * @param context The context of the calling activity or fragment.
     * @param binding The binding object for the search user item layout.
     * @param user The user object for which the status is checked.
     */

    private fun onChangeStatus(context: Context, binding: LayoutSearchUserBinding, user: Users) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val myPath = firebaseUser?.uid
        val otherPath = user.userid
        val generalPath = "Add_Info"
        val query = FirebaseFirestore.getInstance()
            .collection(generalPath)
            .document(myPath.toString())
            .collection("friend")
            .whereEqualTo("otheruserid", otherPath)

        query.get().addOnCompleteListener { myTask ->
            if (myTask.isSuccessful) {
                val myFriendshipDocs = myTask.result?.documents
                if (!myFriendshipDocs.isNullOrEmpty()) {
                    binding.addButton.text = "Remove"
                    binding.addButton.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red1
                        )
                    )
                } else {
                    binding.addButton.text = "Add"
                    binding.addButton.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.blue1
                        )
                    )
                }
            } else {
                binding.addButton.text = "Add"
                binding.addButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue1))
            }
            binding.addButton.visibility = View.VISIBLE
        }
    }

    /**
     * Function to show the add/remove user dialog.
     *
     * @param context The context of the calling activity or fragment.
     * @param user The user object for which the dialog is displayed.
     * @param status The current status of the user (add or remove).
     * @param binding The binding object for the search user item layout.
     */

    private fun showDialogAdd(
        context: Context,
        user: Users,
        status: String,
        binding: LayoutSearchUserBinding
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

            viewBinding.cancelButton.setOnClickListener {VibrationUtil.vibrate(context)

             dialog.dismiss() }
            viewBinding.yesButton.setOnClickListener {VibrationUtil.vibrate(context)

            
                dialog.dismiss()
                if (myPath != null && otherPath != null) {
                    firestore.collection(generalPath).document(myPath).collection("friend")
                        .add(myDataHashMap)
                    firestore.collection(generalPath).document(otherPath).collection("friend")
                        .add(otherDataHashMap)
                }
                binding.addButton.text = "Remove"
                binding.addButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red1))

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

            viewBinding.cancelButton.setOnClickListener {VibrationUtil.vibrate(context)

             dialog.dismiss() }
            viewBinding.yesButton.setOnClickListener {VibrationUtil.vibrate(context)

            
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
                        binding.addButton.text = "Add"
                        binding.addButton.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.blue1
                            )
                        )
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
}