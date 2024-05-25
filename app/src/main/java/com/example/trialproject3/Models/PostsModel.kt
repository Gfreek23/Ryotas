package com.example.trialproject3.Models

import java.io.Serializable

data class PostsModel(
    val postID: String = "",
    val userID: String = "",
    val fullName: String = "",
    val userType: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val title: String = "",
    val titleLowerCase: String = "",
    val titleWords: List<String> = listOf(),
    val description: String = "",
    val postCategory: String = "",
    val postImage: String = "",
    val userPostImage: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val timePosted: String = ""
) : Serializable
