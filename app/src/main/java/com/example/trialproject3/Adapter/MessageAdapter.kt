package com.example.trialproject3.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trialproject3.Models.MessageModel
import com.example.trialproject3.databinding.ItemReceivedMessageBinding
import com.example.trialproject3.databinding.ItemSentMessageBinding

class MessageAdapter(
    private val messagesModelList: List<MessageModel>,
    private val senderID: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        return if (messagesModelList[position].senderID == senderID)
            VIEW_TYPE_SENT
        else
            VIEW_TYPE_RECEIVED
    }

    class SentMessageViewHolder(val binding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messageModel: MessageModel) {
            binding.sentMessageTextView.text = messageModel.message
            binding.timeAndDateTextView.text = messageModel.messageTime.toString()
        }
    }

    class ReceivedMessageViewHolder(val binding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messageModel: MessageModel) {
            binding.receivedMessageTextView.text = messageModel.message
            binding.timeAndDateTextView.text = messageModel.messageTime.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        } else {
            ReceivedMessageViewHolder(
                ItemReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_SENT)
            (holder as? SentMessageViewHolder)?.bind(messagesModelList[position])
        else (holder as? ReceivedMessageViewHolder)?.bind(messagesModelList[position])
    }

    override fun getItemCount() = messagesModelList.size
}