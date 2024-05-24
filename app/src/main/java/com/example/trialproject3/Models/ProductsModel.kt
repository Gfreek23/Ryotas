package com.example.trialproject3.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

//@Parcelize
data class ProductsModel(
    val sellerUserID: String = "",
    val userType: String = "",
    val sellerName: String = "",
    val sellerProfilePicture: String = "",
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val productName: String = "",
    val productNameLowercase: String = "",
    val productNameWords: List<String> = listOf(), // new field
    val productDescription: String = "",
    val productCategory: String = "",
    val price: Double = 0.0,
    val productImage: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val timePosted: String = ""
) : Serializable