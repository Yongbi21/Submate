package com.example.chatmessenger.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatmessenger.R
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.utility.VibrationUtil
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Adapter for the RecyclerView displaying a list of users.
 * Responsible for inflating user list item views and binding user data to them.
 *
 * @property listOfUsers List of users to display.
 * @property listener Listener to handle user item click events.
 */


class UserAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = listOf<Users>()
    private var listener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem, parent, false)
        return UserHolder(view)

    }

    override fun getItemCount(): Int {

        return listOfUsers.size



    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {

        val users = listOfUsers[position]

        val name = users.username!!.split("\\s".toRegex())[0]
        holder.profileName.text = name
        holder.profileName.textSize =  FontSizeHelper.getFontRecentMessageDescriptioSize(holder.itemView.context)

        FontFamilyHelper.applyFontFamily(holder.itemView.context, holder.profileName)


        if (users.status.equals("Online")){

            holder.statusImageView.setImageResource(R.drawable.onlinestatus)


        } else {
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)


        }


        Glide.with(holder.itemView.context).load(users.imageUrl).into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            VibrationUtil.vibrate(holder.itemView.context)

            
            listener?.onUserSelected(position, users)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }


}

/**
 * ViewHolder for the user item view.
 * Responsible for holding references to views within the item layout.
 *
 * @property itemView The view for the user item.
 */

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val profileName: TextView = itemView.findViewById(R.id.userName)
    val imageProfile : CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)



}

/**
 * Called when a user item is clicked.
 *
 * @param position The position of the clicked item in the list.
 * @param users The user object associated with the clicked item.
 */

interface OnItemClickListener{
    fun onUserSelected(position: Int, users: Users)
}