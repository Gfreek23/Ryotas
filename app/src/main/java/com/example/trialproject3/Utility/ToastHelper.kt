package com.example.trialproject3.Utility

import android.content.Context
import android.widget.Toast

class ToastHelper(private val context : Context) {
    fun showToast(text: String, length : Int){
        Toast.makeText(context, text, length).show()
    }
}