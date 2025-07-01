package com.example.chamberlyab

import com.example.chamberlyab.data.Comment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamberlyab.adapters.CommentAdapter
import com.example.chamberlyab.databinding.BottomSheetCommentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentBottomSheet(private val postId: String, private val dreamText: String) : BottomSheetDialogFragment()
 {

    private var _binding: BottomSheetCommentBinding? = null
    private val binding get() = _binding!!
    private val comments = mutableListOf<Comment>()
    private lateinit var adapter: CommentAdapter

    private val commentKeys = mutableListOf<String>()


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CommentAdapter(comments, commentKeys, postId)
        binding.tvDreamText.text = dreamText
        binding.recyclerViewComments.adapter = adapter
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())

        loadComments()

        binding.btnSendComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                sendComment(text)
                binding.etComment.setText("")
            }
        }
    }

     private fun loadComments() {
         val ref = FirebaseDatabase.getInstance().getReference("posts/$postId/comments")
         ref.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 comments.clear()
                 commentKeys.clear()
                 for (child in snapshot.children) {
                     val comment = child.getValue(Comment::class.java)
                     if (comment != null) {
                         comments.add(comment)
                         commentKeys.add(child.key!!) // Add the comment's Firebase key
                     }
                 }
                 adapter.notifyDataSetChanged()
             }

             override fun onCancelled(error: DatabaseError) {
                 Toast.makeText(context, "Failed to load comments", Toast.LENGTH_SHORT).show()
             }
         })
     }

     private fun sendComment(text: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val comment = Comment(
            userId = user.uid,
            userName = user.displayName ?: "Anonymous",
            userPhotoUrl = user.photoUrl?.toString() ?: "",
            commentText = text,
            timestamp = System.currentTimeMillis()
        )
        val ref = FirebaseDatabase.getInstance().getReference("posts/$postId/comments").push()
        ref.setValue(comment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
