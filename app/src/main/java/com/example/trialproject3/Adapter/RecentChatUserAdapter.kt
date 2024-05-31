package com.example.trialproject3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Models.RecentChatUserModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ItemChatBinding

class RecentChatUserAdapter(
    private val context: Context,
    private val recentChatModelList: List<RecentChatUserModel>,
    private val onRecentChatUserClickListener: OnRecentChatUserClickListener
) :
    RecyclerView.Adapter<RecentChatUserAdapter.ChatViewHolder>() {


    interface OnRecentChatUserClickListener {
        fun onRecentChatUserClick(recentChatUserModel: RecentChatUserModel)
    }

    class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context,
            recentChatUserModel: RecentChatUserModel,
            onRecentChatUserClickListener: OnRecentChatUserClickListener
        ) {
            binding.recentMessageTimeTextView.visibility = View.GONE
            binding.chatNameTextView.text = recentChatUserModel.chatUserName
            binding.recentMessageTextView.text = recentChatUserModel.recentMessage

            if (recentChatUserModel.recentMessageTime != 0L){
                binding.recentMessageTimeTextView.visibility = View.VISIBLE
                binding.recentMessageTimeTextView.text =
                    recentChatUserModel.recentMessageTime.toString()
            }


            if (recentChatUserModel.chatUserProfilePicture != "none") {
                Glide.with(context)
                    .load(recentChatUserModel.chatUserProfilePicture)
                    .placeholder(R.drawable.loading_gif)
                    .into(binding.chatUserProfilePicture)
            }

            itemView.setOnClickListener {
                onRecentChatUserClickListener.onRecentChatUserClick(
                    recentChatUserModel
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(
            context,
            recentChatModelList[position],
            onRecentChatUserClickListener
        )
    }

    override fun getItemCount(): Int {
        return recentChatModelList.size
    }
}