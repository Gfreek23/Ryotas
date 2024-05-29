package com.example.trialproject3.Models

import java.io.Serializable
data class ChatModel(
    val chatID: String = "",
    val senderID: String = "",
    val receiverID: String = "",
    val message: String = "",
    val messageTime: Long = 0L,
    val isRead: Boolean = false
) : Serializable
