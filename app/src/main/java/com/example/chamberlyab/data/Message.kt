package com.example.chamberlyab.data

data class Message(
    val text: String? = null,
    var isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
