package com.example.chamberlyab.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.chamberlyab.R
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.appcheck.FirebaseAppCheck
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG = "GeminiAI"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val model: GenerativeModel = Firebase.ai(
                    backend = GenerativeBackend.vertexAI()
                ).generativeModel("gemini-2.5-flash")

                val prompt = "Write a story about a magic backpack."
                val response = model.generateContent(prompt)

                Log.d(TAG, "AI Response: ${response.text}")
            } catch (e: Exception) {
                Log.e(TAG, "Error generating content", e)
            }
        }
    }
}