package com.example.trialproject3.Models

import java.io.Serializable

data class ShowPostUserModel(
    val userID: String = "",
    val fullName: String = "",
    val userPostImage: String = "",
    val userType: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val title: String = "",
    val description: String = "",
    val postImage: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
) : Serializable
