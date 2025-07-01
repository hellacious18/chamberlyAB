package com.example.chamberlyab.adapters

import android.app.AlertDialog
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chamberlyab.R
import com.example.chamberlyab.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CommentAdapter(
    private val comments: List<Comment>,
    private val commentKeys: List<String>,
    private val postId: String
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgUser: ImageView = view.findViewById(R.id.imgCommentUser)
        val txtName: TextView = view.findViewById(R.id.tvCommentUser)
        val txtComment: TextView = view.findViewById(R.id.tvCommentText)

        val txtTimestamp: TextView = view.findViewById(R.id.tvTimestamp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        val commentKey = commentKeys[position]
        val context = holder.itemView.context
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        holder.txtName.text = comment.userName
        holder.txtComment.text = comment.commentText
        holder.txtTimestamp.text = formatTimestamp(comment.timestamp)
        Glide.with(context).load(comment.userPhotoUrl).circleCrop().into(holder.imgUser)

        // Allow deletion only for own comments
        if (comment.userId == currentUserId) {
            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.app_icon)
                    .setTitle("Delete Comment")
                    .setMessage("Do you want to delete your comment?")
                    .setPositiveButton("Delete") { _, _ ->
                        val ref = FirebaseDatabase.getInstance()
                            .getReference("posts/$postId/comments/$commentKey")
                        ref.removeValue()
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    override fun getItemCount() = comments.size
}
