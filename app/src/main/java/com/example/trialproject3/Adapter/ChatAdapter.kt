package com.example.trialproject3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Models.ChatModel
import com.example.trialproject3.Models.RecentChatUsersModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ItemChatBinding

class ChatAdapter(
    private val context: Context,
    private val recentChatModelList: List<RecentChatUsersModel>
) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context, recentChatUsersModel: RecentChatUsersModel
        ) {
            binding.chatNameTextView.text = recentChatUsersModel.userName
            binding.recentMessageTextView.text = recentChatUsersModel.recentMessage
            binding.recentMessageTimeTextView.text =
                recentChatUsersModel.recentMessageTime.toString()

            if (recentChatUsersModel.userImage != "none") {
                Glide.with(context)
                    .load(recentChatUsersModel.userImage)
                    .placeholder(R.drawable.loading_gif).into(binding.chatUserProfilePicture)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(context, recentChatModelList[position])
    }

    override fun getItemCount(): Int {
        return recentChatModelList.size
    }
}