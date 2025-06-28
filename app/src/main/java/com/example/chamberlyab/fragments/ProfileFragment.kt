package com.example.chamberlyab.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chamberlyab.activities.MainActivity
import com.example.chamberlyab.R
import com.example.chamberlyab.adapters.ProfileOptionsAdapter
import com.example.chamberlyab.data.ProfileOption
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val nameTextView = view.findViewById<TextView>(R.id.tvUserName)
        val emailTextView = view.findViewById<TextView>(R.id.tvUserEmail)
        val profileImageView = view.findViewById<ImageView>(R.id.imgProfilePic)
        val listView = view.findViewById<ListView>(R.id.profileListView)


        user?.let {
            nameTextView.text = it.displayName ?: "No Name"
            emailTextView.text = it.email ?: "No Email"

            val photoUrl = it.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.account_circle_24px) // default image
            }
        }

        val options = listOf(
            ProfileOption("Edit Profile", R.drawable.manage_accounts_24px),
            ProfileOption("Settings", R.drawable.settings_24px),
            ProfileOption("Privacy Policy", R.drawable.encrypted_24px),
            ProfileOption("Logout", R.drawable.logout_24px),
            ProfileOption("Delete Account", R.drawable.account_circle_off_24px)
        )

        val adapter = ProfileOptionsAdapter(requireContext(), options)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (options[position].title) {
                "Edit Profile" -> {
                    // TODO: Open Edit Profile Activity
                }

                "Settings" -> {
                    // TODO: Open Settings Activity or Fragment
                }

                "Privacy Policy" -> {
                    // TODO: Show Privacy Policy
                }

                "Logout" -> {
                    (activity as? MainActivity)?.logout()
                }

                "Delete Account" -> {
                    // TODO: Show Delete Account Confirmation Dialog
                }
            }
        }
    }
}