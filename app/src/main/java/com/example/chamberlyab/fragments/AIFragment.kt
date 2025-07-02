package com.example.chamberlyab.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chamberlyab.R
import com.example.chamberlyab.adapters.ChatAdapter
import com.example.chamberlyab.data.Message
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class AIFragment : Fragment(R.layout.fragment_ai) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"
    private val TAG = "DreamWeaverAI"
    private val conversationHistory = mutableListOf<Pair<String, String>>() // Pair<role, message>
    private var summaryContext: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recylerViewHomeFragment)
        inputField = view.findViewById(R.id.inputField)
        sendButton = view.findViewById(R.id.sendButton)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadMessages()

        sendButton.setOnClickListener {
            val dream = inputField.text.toString().trim()
            if (dream.isNotEmpty()) {
                inputField.text.clear()
                sendUserDream(dream)
            }
        }

        val verticalMenu: ImageView = view.findViewById(R.id.verticalMenu)

        verticalMenu.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.menuInflater.inflate(R.menu.vertical_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        confirmDeleteChat()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }

    private fun confirmDeleteChat() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.app_icon)
            .setTitle("Delete Chat")
            .setMessage("Are you sure you want to delete all messages?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAllMessages()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAllMessages() {
        val collectionRef = db.collection("dreamLogs")
            .document(userEmail)
            .collection("messages")

        collectionRef.get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    messages.clear()
                    adapter.notifyDataSetChanged()
                }.addOnFailureListener {
                    Log.e(TAG, "Failed to delete messages", it)
                }
            }
    }


    private fun loadMessages() {
        db.collection("dreamLogs")
            .document(userEmail)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
                //(MetadataChanges.INCLUDE) for caching
            .addSnapshotListener(MetadataChanges.INCLUDE){ snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore error", error)
                    return@addSnapshotListener
                }

                messages.clear()
                snapshot?.forEach { doc ->
                    val data = doc.data
                    val text = data["content"] as? String ?: ""
                    val role = data["role"] as? String ?: "user"
                    val timestamp =
                        (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time
                            ?: System.currentTimeMillis()
                    val isUser = role == "user"

                    messages.add(Message(text = text, isUser = isUser, timestamp = timestamp))
                }

                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
            }
    }

    private fun sendUserDream(dream: String) {
        val message = mapOf(
            "content" to dream,
            "role" to "user",
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("dreamLogs")
            .document(userEmail)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                adapter.addMessage(Message(text = dream, isUser = true))
                recyclerView.scrollToPosition(messages.size - 1)
                interpretDream(dream)
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to send dream", it)
            }
    }

    private fun interpretDream(dream: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val model: GenerativeModel = Firebase.ai(
                    backend = GenerativeBackend.vertexAI()
                ).generativeModel("gemini-2.5-flash")

                // Set system prompt once
                if (conversationHistory.none { it.first == "system" }) {
                    conversationHistory.add(
                        "system" to """
                            You are DreamWeaver, a warm and thoughtful dream interpreter.
                            Offer symbolic insight and emotional meaning to dreams in a calming, conversational tone.
                            Be brief, reflective, and friendly — never cold or robotic.
                        """.trimIndent()
                    )
                }

                // Add new user input
                conversationHistory.add("user" to dream)

                // Use only recent history
                val contextMessage = conversationHistory.first { it.first == "system" }.second
                val recentTurns = conversationHistory.takeLast(4)

                val prompt = buildString {
                    append("System: $contextMessage\n\n")
                    if (summaryContext.isNotBlank()) {
                        append("Summary so far: $summaryContext\n\n")
                    }
                    recentTurns.forEach { (role, message) ->
                        append(
                            when (role) {
                                "user" -> "User: $message"
                                "ai" -> "DreamWeaver: $message"
                                else -> "$role: $message"
                            }
                        )
                        append("\n\n")
                    }
                }.trim()

                Log.d(TAG, "Sending prompt to Gemini:\n$prompt")

                val response = model.generateContent(prompt)
                val reply = response.text ?: "I'm still pondering that dream. 💤"

                // Add AI reply
                conversationHistory.add("ai" to reply)

                // Save to Firestore
                val aiMessage = mapOf(
                    "content" to reply,
                    "role" to "ai",
                    "timestamp" to FieldValue.serverTimestamp()
                )

                db.collection("dreamLogs")
                    .document(userEmail)
                    .collection("messages")
                    .add(aiMessage)

                adapter.addMessage(Message(text = reply, isUser = false))
                recyclerView.scrollToPosition(messages.size - 1)

                // ⏱ Summarize if history is growing
                val exchangeCount = conversationHistory.count { it.first == "user" || it.first == "ai" }
                if (exchangeCount >= 6) {
                    val convoOnly = conversationHistory
                        .filter { it.first == "user" || it.first == "ai" }
                        .joinToString("\n") { "${it.first}: ${it.second}" }

                    val summarizationPrompt = """
                        System: Summarize this conversation between a user and a dream interpreter in a warm, emotionally aware way, preserving symbolic interpretations and user emotions:

                        $convoOnly
                    """.trimIndent()

                    val summaryResponse = model.generateContent(summarizationPrompt)
                    summaryContext = summaryResponse.text ?: summaryContext

                    // Keep only system + last 2 turns
                    val lastFew = conversationHistory.takeLast(4)
                    conversationHistory.clear()
                    conversationHistory.add(conversationHistory.first { it.first == "system" })
                    conversationHistory.addAll(lastFew)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Gemini generation failed", e)
                adapter.addMessage(
                    Message(
                        text = "Sorry, I couldn't interpret that dream right now.",
                        isUser = false
                    )
                )
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }
}
