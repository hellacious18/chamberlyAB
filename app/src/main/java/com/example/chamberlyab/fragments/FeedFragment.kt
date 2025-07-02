package com.example.chamberlyab.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamberlyab.bottomsheets.CommentBottomSheet
import com.example.chamberlyab.R
import com.example.chamberlyab.adapters.PostAdapter
import com.example.chamberlyab.data.DreamPost
import com.example.chamberlyab.databinding.FragmentFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PostAdapter
    private val posts = mutableListOf<DreamPost>()

    // JSON parser for caching
    private val gson = Gson()

    // SharedPreferences for local post caching
    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences("feed_cache", Context.MODE_PRIVATE)
    }

    // Inflate view using ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Initialize RecyclerView, FAB, and data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set up adapter with like and comment listeners
        adapter = PostAdapter(posts, this::toggleLike, this::openCommentDialog)
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = adapter

        // Add new post button (FAB)
        binding.fabAddPost.setOnClickListener {
            AddDreamDialog().show(childFragmentManager, "AddDream")
        }

        loadCachedPosts()  // Load from local storage first
        listenForPosts()   // Then listen for live updates from Firebase
    }

    // Load previously cached posts (offline access)
    private fun loadCachedPosts() {
        val json = sharedPrefs.getString("cached_posts", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<DreamPost>>() {}.type
            val cached = gson.fromJson<List<DreamPost>>(json, type)
            posts.clear()
            posts.addAll(cached.shuffled()) // Shuffle to simulate randomness
            adapter.notifyDataSetChanged()
        }
    }

    // Save post list to SharedPreferences as JSON
    private fun cachePostsLocally(postList: List<DreamPost>) {
        val json = gson.toJson(postList)
        sharedPrefs.edit().putString("cached_posts", json).apply()
    }

    // Realtime listener for all dream posts in Firebase
    private fun listenForPosts() {
        val ref = FirebaseDatabase.getInstance().getReference("posts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear()
                for (child in snapshot.children) {
                    val post = child.getValue(DreamPost::class.java)
                    post?.let { posts.add(it) }
                }

                posts.shuffle()             // Randomize post order
                cachePostsLocally(posts)    // Cache for offline use
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load posts", Toast.LENGTH_SHORT).show()
                Log.e("FEED", "Error loading posts: ${error.message}")
            }
        })
    }

    // Like/unlike a post
    private fun toggleLike(post: DreamPost) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance()
            .getReference("posts/${post.postId}/likes/$uid")

        if (post.likes.containsKey(uid)) {
            ref.removeValue() // Unlike
        } else {
            ref.setValue(true) // Like
        }
    }

    // Show bottom sheet to comment on a post
    private fun openCommentDialog(post: DreamPost) {
        val dialog = CommentBottomSheet(post.postId, post.dreamText)
        dialog.show(childFragmentManager, "CommentSheet")
    }

    // Clean up view binding reference to prevent memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
