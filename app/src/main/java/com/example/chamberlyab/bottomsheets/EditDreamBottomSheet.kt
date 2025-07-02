package com.example.chamberlyab.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import com.example.chamberlyab.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// A BottomSheetDialogFragment to allow users to edit an existing dream entry
class EditDreamBottomSheet(
    private val date: String,             // Used as document ID in Firestore
    private val currentTitle: String,     // Current title of the dream
    private val currentContent: String    // Current content of the dream
) : BottomSheetDialogFragment() {

    // Get the current user's email or use a fallback
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"

    // Inflate the bottom sheet layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit_dream, container, false)
    }

    // Called after the view is created. Set up the UI and logic here.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etTitle = view.findViewById<EditText>(R.id.etDreamTitle)         // Title input
        val etContent = view.findViewById<EditText>(R.id.etDreamContent)     // Content input
        val btnSave = view.findViewById<TextView>(R.id.btnSaveDream)         // Save button (TextView used for styling)

        // Pre-fill input fields with the existing title and content
        etTitle.setText(currentTitle)
        etContent.setText(currentContent)

        // Save button logic
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            val email = FirebaseAuth.getInstance().currentUser?.email

            if (email != null) {
                // Update the dream document in Firestore
                FirebaseFirestore.getInstance()
                    .collection("userDreams")
                    .document(userEmail)
                    .collection("dreams")
                    .document(date)
                    .set(mapOf("title" to title, "content" to content))
                    .addOnSuccessListener {
                        if (isAdded) {
                            // Notify the calling fragment that the dream was updated
                            setFragmentResult("dream_updated", Bundle.EMPTY)
                            dismiss()
                        }
                    }
                    .addOnFailureListener {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to save dream: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}