package com.example.chatmessenger.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.helper.FontFamilyHelper
import com.example.chatmessenger.helper.FontSizeHelper
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.utility.VibrationUtil

class MessageAdapter(private val messageClickListener: MessageClickListener) :
    RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    private var listOfMessage = listOf<Messages>()
    private var fontSize: Float = 16f

    private val LEFT = 0
    private val RIGHT = 1

    interface MessageClickListener {
        fun onMessageClick(message: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.chatitemright, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.chatitemleft, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE
        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""
        holder.timeOfSent.textSize =
            FontSizeHelper.getFontSmallDescriptionSize(holder.itemView.context)

        // Set the font size for the message text
        holder.messageText.textSize = FontSizeHelper.getFontDescriptionSize(holder.itemView.context)


        FontFamilyHelper.applyFontFamily(holder.itemView.context, holder.messageText)
        FontFamilyHelper.applyFontFamily(holder.itemView.context, holder.timeOfSent)

        // Set OnClickListener for the messageText TextView
        holder.messageText.setOnClickListener {
            VibrationUtil.vibrate(holder.itemView.context)

             VibrationUtil.vibrate(holder.itemView.context)
            // Use the holder's adapter position to get the correct message
            val clickedMessage = listOfMessage[holder.adapterPosition]
            messageClickListener.onMessageClick(clickedMessage.message)
        }
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == Utils.getUidLoggedIn()) RIGHT else LEFT

    fun setList(newList: List<Messages>) {
        this.listOfMessage = newList
        notifyDataSetChanged()
    }

    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.show_message)
        val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
    }
}
