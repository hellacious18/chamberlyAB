package com.example.chamberlyab.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamberlyab.CommentBottomSheet
import com.example.chamberlyab.R
import com.example.chamberlyab.adapters.PostAdapter
import com.example.chamberlyab.data.DreamPost
import com.example.chamberlyab.databinding.FragmentFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostAdapter
    private val posts = mutableListOf<DreamPost>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PostAdapter(posts, this::toggleLike, this::openCommentDialog)
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = adapter

        binding.fabAddPost.setOnClickListener {
            AddDreamDialog().show(childFragmentManager, "AddDream")
        }

        listenForPosts()
    }

    private fun listenForPosts() {
        val ref = FirebaseDatabase.getInstance().getReference("posts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear()
                for (child in snapshot.children) {
                    val post = child.getValue(DreamPost::class.java)
                    post?.let { posts.add(it) }
                }
                posts.sortByDescending { it.timestamp }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load comments", Toast.LENGTH_SHORT).show()
                Log.e("COMMENTS", "Error loading comments: ${error.message}")
            }        })
    }

    private fun toggleLike(post: DreamPost) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance()
            .getReference("posts/${post.postId}/likes/$uid")

        if (post.likes.containsKey(uid)) {
            ref.removeValue()
        } else {
            ref.setValue(true)
        }
    }

    private fun openCommentDialog(post: DreamPost) {
        val dialog = CommentBottomSheet(post.postId, post.dreamText)
        dialog.show(childFragmentManager, "CommentSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

