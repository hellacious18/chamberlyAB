package com.example.chamberlyab.adapters

// Required Android and Kotlin imports
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

// Adapter class for displaying chat messages in a RecyclerView
class ChatAdapter(val messages: MutableList<Message>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    // ViewHolder class that holds references to the views for each item
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText) // Text view for the message content
        val messageContainer: LinearLayout = itemView.findViewById(R.id.messageLayout) // Container for aligning message
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflate the layout for an individual chat message
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    // Called by RecyclerView to display data at the specified position
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position] // Get the current message
        val markwon = Markwon.create(holder.itemView.context) // Initialize Markwon for markdown rendering

        // Clear any existing text in the message view
        holder.messageText.text = null

        // Set message alignment and background based on sender
        if (message.isUser) {
            holder.messageContainer.gravity = Gravity.END // Align user message to the right
            holder.messageText.setBackgroundResource(R.drawable.user_message_bubble) // Set background for user message
        } else {
            holder.messageContainer.gravity = Gravity.START // Align AI message to the left
            holder.messageText.setBackgroundResource(0) // Remove background for AI message
        }

        // Handle message visibility and markdown rendering
        if (!message.text.isNullOrEmpty()) {
            holder.messageText.visibility = View.VISIBLE // Show message if it's not empty
            if (message.isUser) {
                holder.messageText.text = message.text // Plain text for user message
            } else {
                markwon.setMarkdown(holder.messageText, message.text) // Render markdown for AI message
            }
        } else {
            holder.messageText.visibility = View.GONE // Hide the message if it's empty or null
        }
    }

    // Returns the total number of messages
    override fun getItemCount(): Int = messages.size

    // Updates the last AI message with new text
    fun updateLastMessageText(newText: String) {
        if (messages.isNotEmpty()) {
            val lastMessage = messages.last()
            if (!lastMessage.isUser) { // Only update if the last message is from AI
                messages[messages.size - 1] = lastMessage.copy(text = newText) // Replace the last message with updated text
                notifyItemChanged(messages.size - 1) // Notify RecyclerView to refresh that item
            }
        }
    }

    // Adds a new message to the list
    fun addMessage(message: Message) {
        messages.add(message) // Add new message to list
        notifyItemInserted(messages.size - 1) // Notify RecyclerView to insert new item
    }
}
