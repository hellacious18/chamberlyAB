package com.example.chamberlyab.adapters


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

class PostAdapter(
    private val posts: List<DreamPost>,
    private val onLikeClick: (DreamPost) -> Unit,
    private val onCommentClick: (DreamPost) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgUser: ImageView = view.findViewById(R.id.imgUser)
        val txtName: TextView = view.findViewById(R.id.txtUserName)
        val txtDream: TextView = view.findViewById(R.id.txtDreamText)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
        val txtLikeCount: TextView = view.findViewById(R.id.txtLikeCount)
        val txtCommentCount: TextView = view.findViewById(R.id.txtCommentCount)
        val btnComment: ImageView = view.findViewById(R.id.btnComment)
        val txtTimestamp: TextView = view.findViewById(R.id.txtTimestamp)

        val btnPostMenu: ImageView = view.findViewById(R.id.btnPostMenu)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context
        val likeCount = post.likes.size
        val commentCount = post.comments?.size ?: 0
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val liked = post.likes.containsKey(uid)


        holder.txtName.text = post.userName
        holder.txtDream.text = post.dreamText
        Glide.with(context).load(post.userPhotoUrl).circleCrop().into(holder.imgUser)

        holder.txtLikeCount.text = "$likeCount like${if (likeCount == 1) "" else "s"}"

        holder.txtCommentCount.text = "$commentCount comment${if (commentCount == 1) "" else "s"}"

        holder.btnLike.setImageResource(if (liked) R.drawable.like_red_24px else R.drawable.like_24px)

        holder.btnLike.setOnClickListener { onLikeClick(post) }
        holder.btnComment.setOnClickListener { onCommentClick(post) }
        holder.txtTimestamp.text = formatTimestamp(post.timestamp)
        holder.btnPostMenu.visibility = if (post.userId == uid) View.VISIBLE else View.GONE

        holder.btnPostMenu.setOnClickListener {
            val popup = PopupMenu(context, holder.btnPostMenu)
            popup.inflate(R.menu.vertical_menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog(post.postId, context)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun showDeleteConfirmationDialog(postId: String, context: Context) {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.app_icon)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                val ref = com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("posts").child(postId)
                ref.removeValue()
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    override fun getItemCount() = posts.size


}
