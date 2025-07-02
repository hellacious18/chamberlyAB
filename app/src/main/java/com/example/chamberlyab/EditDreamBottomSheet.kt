package com.example.chamberlyab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditDreamBottomSheet(
    private val date: String,
    private val currentTitle: String,
    private val currentContent: String
) : BottomSheetDialogFragment() {

    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit_dream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etTitle = view.findViewById<EditText>(R.id.etDreamTitle)
        val etContent = view.findViewById<EditText>(R.id.etDreamContent)
        val btnSave = view.findViewById<TextView>(R.id.btnSaveDream)

        etTitle.setText(currentTitle)
        etContent.setText(currentContent)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()
            val email = FirebaseAuth.getInstance().currentUser?.email

            if (email != null) {
                val fragmentManager = parentFragmentManager

                FirebaseFirestore.getInstance()
                    .collection("userDreams")
                    .document(userEmail)
                    .collection("dreams")
                    .document(date)
                    .set(mapOf("title" to title, "content" to content))
                    .addOnSuccessListener {
                        if (isAdded) {
                            setFragmentResult("dream_updated", Bundle.EMPTY)
                            dismiss()
                        }
                    }

                    .addOnFailureListener {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Failed to save dream: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }

    }
}