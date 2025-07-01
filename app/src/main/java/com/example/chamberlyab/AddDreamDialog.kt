package com.example.chamberlyab.fragments

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
import java.util.*

class AddDreamDialog : DialogFragment() {

    private var _binding: DialogAddDreamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddDreamBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Share your dream")
            .setView(binding.root)
            .setCancelable(true)

        binding.btnPostDream.setOnClickListener {
            val dreamText = binding.etDreamText.text.toString().trim()
            if (dreamText.isEmpty()) {
                Toast.makeText(context, "Please write something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            postDream(dreamText)
        }

        return builder.create()
    }

    private fun postDream(dreamText: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = UUID.randomUUID().toString()
        val dream = DreamPost(
            postId = postId,
            userId = user.uid,
            userName = user.displayName ?: "Anonymous",
            userPhotoUrl = user.photoUrl?.toString() ?: "",
            dreamText = dreamText,
            timestamp = System.currentTimeMillis()
        )

        val ref = FirebaseDatabase.getInstance().getReference("posts").child(postId)
        ref.setValue(dream).addOnSuccessListener {
            dismiss()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to post dream", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
