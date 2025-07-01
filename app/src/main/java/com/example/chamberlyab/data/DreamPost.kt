package com.example.chamberlyab.data

data class DreamPost(
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String = "",
    val dreamText: String = "",
    val timestamp: Long = 0L,
    val likes: Map<String, Boolean> = emptyMap(),
    val comments: Map<String, Comment>? = null
)