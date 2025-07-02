package com.example.chamberlyab.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamberlyab.adapters.CommentAdapter
import com.example.chamberlyab.data.Comment
import com.example.chamberlyab.databinding.BottomSheetCommentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentBottomSheet(private val postId: String, private val dreamText: String) : BottomSheetDialogFragment() {

    // View binding to access layout views
    private var _binding: BottomSheetCommentBinding? = null
    private val binding get() = _binding!!

    // List of comments and corresponding Firebase keys
    private val comments = mutableListOf<Comment>()
    private val commentKeys = mutableListOf<String>()

    private lateinit var adapter: CommentAdapter

    // Inflate the bottom sheet view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Set up UI after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize adapter and bind to RecyclerView
        adapter = CommentAdapter(comments, commentKeys, postId)
        binding.recyclerViewComments.adapter = adapter
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())

        // Display the dream text at the top of the bottom sheet
        binding.tvDreamText.text = dreamText

        // Load existing comments from Firebase
        loadComments()

        // Set up listener for sending a new comment
        binding.btnSendComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                sendComment(text)
                binding.etComment.setText("") // Clear input after sending
            }
        }
    }

    // Loads comments for the current post from Firebase Realtime Database
    private fun loadComments() {
        val ref = FirebaseDatabase.getInstance().getReference("posts/$postId/comments")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear current lists to avoid duplicates
                comments.clear()
                commentKeys.clear()

                // Loop through children and add to lists
                for (child in snapshot.children) {
                    val comment = child.getValue(Comment::class.java)
                    if (comment != null) {
                        comments.add(comment)
                        commentKeys.add(child.key!!) // Store Firebase key for later use (e.g., deleting/editing)
                    }
                }
                adapter.notifyDataSetChanged() // Refresh RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load comments", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Sends a new comment to Firebase Realtime Database
    private fun sendComment(text: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        // Create Comment object
        val comment = Comment(
            userId = user.uid,
            userName = user.displayName ?: "Anonymous",
            userPhotoUrl = user.photoUrl?.toString() ?: "",
            commentText = text,
            timestamp = System.currentTimeMillis()
        )

        // Push new comment under the post
        val ref = FirebaseDatabase.getInstance().getReference("posts/$postId/comments").push()
        ref.setValue(comment)
    }

    // Clean up binding to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}