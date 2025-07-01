package com.example.chamberlyab.adapters


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

    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    override fun getItemCount() = posts.size


}
