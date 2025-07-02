package com.example.chamberlyab.data

// Data class representing a chat message (either from the user or the system/AI)
data class Message(
    val text: String? = null,                     // The message text; can be null if not yet loaded
    var isUser: Boolean = false,                  // True if the message was sent by the user
    val timestamp: Long = System.currentTimeMillis()  // Time the message was created (default: current time)
)
