package com.example.chamberlyab.data

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String = "",
    val commentText: String = "",
    val timestamp: Long = 0L
)
