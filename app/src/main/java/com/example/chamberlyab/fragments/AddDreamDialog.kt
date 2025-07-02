package com.example.chamberlyab.fragments

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.chamberlyab.data.DreamPost
import com.example.chamberlyab.databinding.DialogAddDreamBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class AddDreamDialog : DialogFragment() {

    // View binding for the dialog layout
    private var _binding: DialogAddDreamBinding? = null
    private val binding get() = _binding!!

    // Called to create the dialog UI
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the view binding for the custom dialog layout
        _binding = DialogAddDreamBinding.inflate(LayoutInflater.from(context))

        // Build the AlertDialog using the inflated view
        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)

        // Set the click listener for the "Post" button
        binding.btnPostDream.setOnClickListener {
            val dreamText = binding.etDreamText.text.toString().trim()

            // Check if the input is empty
            if (dreamText.isEmpty()) {
                Toast.makeText(context, "Please write something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proceed to post the dream
            postDream(dreamText)
        }

        // Create and return the dialog with a transparent background
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    // Handles posting the dream to Firebase Realtime Database
    private fun postDream(dreamText: String) {
        val user = FirebaseAuth.getInstance().currentUser

        // Ensure user is logged in
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique post ID
        val postId = UUID.randomUUID().toString()

        // Create a new DreamPost object
        val dream = DreamPost(
            postId = postId,
            userId = user.uid,
            userName = user.displayName ?: "Anonymous",
            userPhotoUrl = user.photoUrl?.toString() ?: "",
            dreamText = dreamText,
            timestamp = System.currentTimeMillis()
        )

        // Save the dream to Firebase Realtime Database under "posts/{postId}"
        val ref = FirebaseDatabase.getInstance().getReference("posts").child(postId)
        ref.setValue(dream)
            .addOnSuccessListener {
                // Dismiss the dialog on success
                dismiss()
            }
            .addOnFailureListener {
                // Show an error message if posting fails
                Toast.makeText(context, "Failed to post dream", Toast.LENGTH_SHORT).show()
            }
    }

    // Clean up the binding to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}