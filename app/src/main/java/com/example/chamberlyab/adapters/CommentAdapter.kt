package com.example.chamberlyab.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chamberlyab.R
import com.example.chamberlyab.data.Comment

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

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
        holder.txtName.text = comment.userName
        holder.txtComment.text = comment.commentText
        holder.txtTimestamp.text = formatTimestamp(comment.timestamp)
        Glide.with(holder.itemView.context)
            .load(comment.userPhotoUrl)
            .circleCrop()
            .into(holder.imgUser)
    }
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    override fun getItemCount() = comments.size
}
