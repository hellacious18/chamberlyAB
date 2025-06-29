package com.example.chamberlyab.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamberlyab.R
import com.example.chamberlyab.data.Message
import io.noties.markwon.Markwon

class ChatAdapter(val messages: MutableList<Message>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val messageContainer: LinearLayout = itemView.findViewById(R.id.messageLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val markwon = Markwon.create(holder.itemView.context)

        // Clear existing content
        holder.messageText.text = null

        // Message Alignment and Bubble
        if (message.isUser) {
            holder.messageContainer.gravity = Gravity.END
            holder.messageText.setBackgroundResource(R.drawable.user_message_bubble)
        } else {
            holder.messageContainer.gravity = Gravity.START
            holder.messageText.setBackgroundResource(0)
        }

        if (!message.text.isNullOrEmpty()) {
            holder.messageText.visibility = View.VISIBLE
            if (message.isUser) {
                holder.messageText.text = message.text
            } else {
                markwon.setMarkdown(holder.messageText, message.text)
            }
        } else {
            holder.messageText.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateLastMessageText(newText: String) {
        if (messages.isNotEmpty()) {
            val lastMessage = messages.last()
            if (!lastMessage.isUser) { // Only allow AI message updates
                messages[messages.size - 1] = lastMessage.copy(text = newText)
                notifyItemChanged(messages.size - 1)
            }
        }
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
