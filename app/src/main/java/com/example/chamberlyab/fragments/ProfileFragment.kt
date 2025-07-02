package com.example.chamberlyab.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chamberlyab.activities.MainActivity
import com.example.chamberlyab.R
import com.example.chamberlyab.adapters.ProfileOptionsAdapter
import com.example.chamberlyab.data.ProfileOption
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

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
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Account?")
                        .setMessage("This will delete your posts, chats, and account permanently.")
                        .setPositiveButton("Delete") { _, _ ->
                            deleteUserContentAndAccount()
                            (activity as? MainActivity)?.logout()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()                }
            }
        }
    }
    private fun deleteUserContentAndAccount() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        val userId = user?.uid ?: return
        val userEmail = user.email ?: return

        val rtdb = com.google.firebase.database.FirebaseDatabase.getInstance().reference

        // Step 1: Delete Firestore chat messages
        val chatRef = db.collection("dreamLogs")
            .document(userEmail)
            .collection("messages")

        chatRef.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                Log.d("DELETE_USER", "Chat messages deleted")
            }.addOnFailureListener {
                Log.e("DELETE_USER", "Failed to delete chat messages", it)
            }
        }

        // Step 2: Delete user's posts from Realtime Database
        val postsRef = rtdb.child("posts")
        postsRef.get().addOnSuccessListener { snapshot ->
            for (postSnap in snapshot.children) {
                val postUserId = postSnap.child("userId").getValue(String::class.java)
                if (postUserId == userId) {
                    postSnap.ref.removeValue()
                }
            }
            Log.d("DELETE_USER", "User posts deleted")
        }.addOnFailureListener {
            Log.e("DELETE_USER", "Failed to delete posts", it)
        }

        // Step 3: Delete all dreams under userDreams
        val dreamsRef = db.collection("userDreams")
            .document(userEmail)
            .collection("dreams")

        dreamsRef.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                Log.d("DELETE_USER", "User dreams deleted")
            }.addOnFailureListener {
                Log.e("DELETE_USER", "Failed to delete dreams", it)
            }
        }
    }

}