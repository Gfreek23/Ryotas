package com.example.trialproject3.Models

import java.io.Serializable

data class ProductsModel(
    val productID : String = "",
    val sellerUserID: String = "",
    val userType: String = "",
    val sellerName: String = "",
    val sellerProfilePicture: String = "",
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val productName: String = "",
    val productNameLowercase: String = "",
    val productNameWords: List<String> = listOf(),
    val productDescription: String = "",
    val productCategory: String = "",
    val price: Double = 0.0,
    val productImage: String = "",
    val productRatings: Float = 0.0F,
    val stock : Int = 0,
    val storeName: String = "",
    val storeLocation: String = "",
    val timePosted: String = ""
) : Serializable