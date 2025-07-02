package com.example.chamberlyab.data

// Data class representing a post containing a user's dream entry
data class DreamPost(
    val postId: String = "",                          // Unique identifier for the post
    val userId: String = "",                          // ID of the user who created the post
    val userName: String = "",                        // Display name of the user
    val userPhotoUrl: String = "",                    // URL to the user's profile photo
    val dreamText: String = "",                       // The text content of the dream
    val timestamp: Long = 0L,                         // When the post was created (epoch milliseconds)
    val likes: Map<String, Boolean> = emptyMap(),     // Map of user IDs to true for likes
    val comments: Map<String, Comment>? = null        // Map of comment IDs to Comment objects (nullable)
)
