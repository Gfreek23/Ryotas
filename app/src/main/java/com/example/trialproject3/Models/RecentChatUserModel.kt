package com.example.trialproject3.Models

import java.io.Serializable

data class RecentChatUserModel(
    val chatUserID: String = "",
    val chatUserName: String = "",
    val chatUserProfilePicture: String = "",
    val chatUserType: String = "",
    val chatStoreName: String = "",
    val chatStoreLocation: String = "",
    var recentMessage: String = "",
    var recentMessageTime: Long = 0L,
    val unreadMessageCount: Int = 0
) : Serializable
