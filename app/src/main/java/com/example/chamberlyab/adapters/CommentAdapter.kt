package com.example.chamberlyab.adapters

// Required imports
import android.app.AlertDialog
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chamberlyab.R
import com.example.chamberlyab.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Adapter for displaying comments in a RecyclerView
class CommentAdapter(
    private val comments: List<Comment>,         // List of Comment data objects
    private val commentKeys: List<String>,       // Keys for each comment (for database reference)
    private val postId: String                   // ID of the post that comments belong to
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    // ViewHolder class to hold views for each comment item
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgUser: ImageView = view.findViewById(R.id.imgCommentUser)   // User profile image
        val txtName: TextView = view.findViewById(R.id.tvCommentUser)     // User name text
        val txtComment: TextView = view.findViewById(R.id.tvCommentText)  // Comment text
        val txtTimestamp: TextView = view.findViewById(R.id.tvTimestamp)  // Timestamp of comment
    }

    // Inflates the item layout and returns a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false) // Inflate comment item layout
        return ViewHolder(view)
    }

    // Binds data to the views in the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]                   // Get comment at current position
        val commentKey = commentKeys[position]             // Corresponding Firebase key
        val context = holder.itemView.context              // Context for operations like dialogs
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid // Current logged-in user ID

        // Set comment text and metadata
        holder.txtName.text = comment.userName             // Display commenter’s name
        holder.txtComment.text = comment.commentText       // Display comment text
        holder.txtTimestamp.text = formatTimestamp(comment.timestamp) // Display formatted timestamp

        // Load and display user's profile image using Glide
        Glide.with(context)
            .load(comment.userPhotoUrl)
            .circleCrop()
            .into(holder.imgUser)

        // Enable comment deletion only if the current user is the author
        if (comment.userId == currentUserId) {
            holder.itemView.setOnLongClickListener {
                // Show confirmation dialog
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.app_icon)
                    .setTitle("Delete Comment")
                    .setMessage("Do you want to delete your comment?")
                    .setPositiveButton("Delete") { _, _ ->
                        // Remove comment from Firebase Realtime Database
                        val ref = FirebaseDatabase.getInstance()
                            .getReference("posts/$postId/comments/$commentKey")
                        ref.removeValue() // Delete comment node
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null) // Cancel deletion
                    .show()
                true // Indicate long-click was handled
            }
        }
    }

    // Helper function to format the timestamp into a readable string
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp)) // Format date
    }

    // Returns the number of comments
    override fun getItemCount() = comments.size
}
