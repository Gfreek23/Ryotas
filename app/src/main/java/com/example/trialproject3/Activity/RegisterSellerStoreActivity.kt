package com.example.trialproject3.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trialproject3.Map.MapboxMapActivity
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ActivityRegisterSellerStoreBinding

class RegisterSellerStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterSellerStoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterSellerStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}