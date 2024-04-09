package com.example.trialproject3.Models

data class StoreDetailsModel(
    val storeID: String = "",
    val storeName: String = "",
    val storeOwnerID: String = "",
    val storeOwner: String = "",
    val storeDescription: String = "",
    val storeLocation: String = "",
    val storeLongitude: Double = 0.0,
    val storeLatitude: Double = 0.0,
)
