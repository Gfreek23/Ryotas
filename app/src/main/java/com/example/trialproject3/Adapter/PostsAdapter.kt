package com.example.trialproject3.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ItemPostsBinding

class PostsAdapter(
    private val context: Context,
    private val postsList: List<PostsModel>,
    private val onPostItemClickListener: OnPostItemClickListener,
    private val onPostRateClickListener: OnPostRateClickListener
) :
    RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

    interface OnPostItemClickListener {
        fun onPostItemClick(postsModel: PostsModel)
    }

    interface OnPostRateClickListener {
        fun onPostRateClick(postsModel: PostsModel, rating: Float)
    }

    class PostsViewHolder(val binding: ItemPostsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            postsModel: PostsModel,
            onPostItemClickListener: OnPostItemClickListener,
            onPostRateClickListener: OnPostRateClickListener
        ) {
            binding.titleTextView.text = postsModel.title
            binding.userNameTextView.text = postsModel.fullName
            binding.descriptionTextView.text = postsModel.description
            binding.categoryTextView.text = postsModel.postCategory
            binding.emailTextView.text = postsModel.email
            binding.timePostedTextView.text = postsModel.timePosted
            binding.postImageView.visibility = View.GONE

            if (postsModel.userPostImage != "none") {
                Glide.with(context).load(postsModel.userPostImage)
                    .placeholder(R.drawable.loading_gif)
                    .into(binding.userImageView)
            }
            if (postsModel.postImage != "none") {
                binding.postImageView.visibility = View.VISIBLE
                Glide.with(context).load(postsModel.postImage)
                    .placeholder(R.drawable.loading_gif)
                    .into(binding.postImageView)
            }
            val sharedPreferences =
                context.getSharedPreferences("currentUserPrefs", Context.MODE_PRIVATE)
            val userType = sharedPreferences.getString("userType", null)

            if (userType == "Buyer")
                itemView.setOnClickListener {
                    onPostItemClickListener.onPostItemClick(postsModel)
                }


            if (userType == "Seller") {
                binding.postRatingBar.visibility = View.GONE
                binding.rateBtn.visibility = View.GONE
            }

            binding.postRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                binding.rateBtn.setOnClickListener {
                    onPostRateClickListener.onPostRateClick(postsModel, rating)
                }
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
        holder.bind(context, postsList[position], onPostItemClickListener, onPostRateClickListener)
    }
}