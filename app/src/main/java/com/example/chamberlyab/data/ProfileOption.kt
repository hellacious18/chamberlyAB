package com.example.chamberlyab.data

// Data class representing an option in the profile/settings menu
data class ProfileOption(
    val title: String,          // The display title of the option (e.g., "Edit Profile", "Delete Account")
    val iconResId: Int          // Resource ID for the option's icon (e.g., R.drawable.ic_edit)
)
