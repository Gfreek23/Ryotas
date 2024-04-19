package com.example.trialproject3.Models

data class CartModel(
    val cartID : String = "",
    val cartUserID: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val price: Double = 0.0,
    val productImage: String = "",
    var quantity: Int = 0,
    val storeName: String = "",
    val storeLocation: String = "",
    val timeAdded: String = ""
)
