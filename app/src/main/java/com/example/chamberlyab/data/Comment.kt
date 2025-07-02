package com.example.chamberlyab.data

// Data class representing a comment on a post
data class Comment(
    val userId: String = "",           // ID of the user who made the comment
    val userName: String = "",         // Name of the user
    val userPhotoUrl: String = "",     // URL of the user's profile picture
    val commentText: String = "",      // The text content of the comment
    val timestamp: Long = 0L           // Timestamp of when the comment was posted (epoch millis)
)
