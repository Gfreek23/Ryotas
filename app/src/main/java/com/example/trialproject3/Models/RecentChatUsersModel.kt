package com.example.trialproject3.Models

import java.io.Serializable

data class RecentChatUsersModel(
    val userID: String = "",
    val userName: String = "",
    val userImage: String = "",
    val recentMessage: String = "",
    val recentMessageTime: Long = 0L,
    val unreadMessageCount: Int = 0
) : Serializable
