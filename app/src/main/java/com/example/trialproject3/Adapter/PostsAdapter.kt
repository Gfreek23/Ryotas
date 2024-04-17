package com.example.trialproject3.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.databinding.ItemPostsBinding

class PostsAdapter(
    private val context: Context,
    private val postsList: List<PostsModel>,
    private val onPostItemClickListener: OnPostItemClickListener
) :
    RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

    interface OnPostItemClickListener {
        fun onPostItemClick(postsModel: PostsModel)
    }

    class PostsViewHolder(val binding: ItemPostsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, postsModel: PostsModel, onPostItemClickListener: OnPostItemClickListener) {
            binding.titleTextView.text = postsModel.title
            binding.userNameTextView.text = postsModel.fullName
            binding.descriptionTextView.text = postsModel.description
            binding.emailTextView.text = postsModel.email
            binding.postImageView.visibility = View.GONE

            if (postsModel.userPostImage != "none") {
                Glide.with(context).load(postsModel.userPostImage).into(binding.userImageView)
            }
            if (postsModel.postImage != "none") {
                binding.postImageView.visibility = View.VISIBLE
                Glide.with(context).load(postsModel.postImage).into(binding.postImageView)
            }

            binding.postHeaderLayout.setOnClickListener {
                onPostItemClickListener.onPostItemClick(postsModel)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ItemPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PostsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(context, postsList[position], onPostItemClickListener)
    }
}