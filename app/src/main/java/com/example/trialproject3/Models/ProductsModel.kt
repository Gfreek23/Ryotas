package com.example.trialproject3.Models

data class ProductsModel(
    val sellerUserID: String = "",
    val userType: String = "",
    val sellerName: String = "",
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val price: Double = 0.0,
    val productImage: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val timePosted: String = ""
)