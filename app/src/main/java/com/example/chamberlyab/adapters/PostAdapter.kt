package com.example.chamberlyab.adapters

// Required imports
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chamberlyab.R
import com.example.chamberlyab.data.DreamPost
import com.google.firebase.auth.FirebaseAuth

// Adapter to display a list of DreamPost items in a RecyclerView
class PostAdapter(
    private val posts: List<DreamPost>,                     // List of post data
    private val onLikeClick: (DreamPost) -> Unit,           // Callback for like button click
    private val onCommentClick: (DreamPost) -> Unit         // Callback for comment button click
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // ViewHolder holds references to the UI components of a single post item
    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgUser: ImageView = view.findViewById(R.id.imgUser)                 // User's profile picture
        val txtName: TextView = view.findViewById(R.id.txtUserName)              // User's name
        val txtDream: TextView = view.findViewById(R.id.txtDreamText)            // Dream text content
        val btnLike: ImageView = view.findViewById(R.id.btnLike)                 // Like button
        val txtLikeCount: TextView = view.findViewById(R.id.txtLikeCount)        // Display number of likes
        val txtCommentCount: TextView = view.findViewById(R.id.txtCommentCount)  // Display number of comments
        val btnComment: ImageView = view.findViewById(R.id.btnComment)           // Comment button
        val txtTimestamp: TextView = view.findViewById(R.id.txtTimestamp)        // Post timestamp
        val btnPostMenu: ImageView = view.findViewById(R.id.btnPostMenu)         // Menu button for post actions (e.g., delete)
    }

    // Inflates the layout for a post item and returns a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    // Binds data to the views in the ViewHolder
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]                            // Get the current post
        val context = holder.itemView.context                 // Get context from the view
        val likeCount = post.likes.size                       // Total number of likes
        val commentCount = post.comments?.size ?: 0           // Total number of comments (safe for null)
        val uid = FirebaseAuth.getInstance().currentUser?.uid // Current logged-in user ID
        val liked = post.likes.containsKey(uid)               // Check if user already liked the post

        // Set user details and post content
        holder.txtName.text = post.userName
        holder.txtDream.text = post.dreamText
        Glide.with(context).load(post.userPhotoUrl).circleCrop().into(holder.imgUser) // Load user photo

        // Set like count text with proper pluralization
        holder.txtLikeCount.text = "$likeCount like${if (likeCount == 1) "" else "s"}"

        // Set comment count text with proper pluralization
        holder.txtCommentCount.text = "$commentCount comment${if (commentCount == 1) "" else "s"}"

        // Set like button image depending on whether post is liked
        holder.btnLike.setImageResource(if (liked) R.drawable.like_red_24px else R.drawable.like_24px)

        // Set click listeners for like and comment actions
        holder.btnLike.setOnClickListener { onLikeClick(post) }
        holder.btnComment.setOnClickListener { onCommentClick(post) }

        // Format and display timestamp
        holder.txtTimestamp.text = formatTimestamp(post.timestamp)

        // Show post menu button only for the owner of the post
        holder.btnPostMenu.visibility = if (post.userId == uid) View.VISIBLE else View.GONE

        // Handle click on the post menu button (e.g., delete option)
        holder.btnPostMenu.setOnClickListener {
            val popup = PopupMenu(context, holder.btnPostMenu) // Create popup menu
            popup.inflate(R.menu.vertical_menu)                // Inflate menu resource

            // Handle menu item click events
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog(post.postId, context) // Confirm before deleting
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    // Shows a confirmation dialog before deleting a post
    private fun showDeleteConfirmationDialog(postId: String, context: Context) {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.app_icon)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                // Remove the post from Firebase
                val ref = com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("posts").child(postId)
                ref.removeValue()
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null) // Cancel action
            .show()
    }

    // Formats the timestamp into a readable string
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    // Returns the total number of posts in the list
    override fun getItemCount() = posts.size
}
