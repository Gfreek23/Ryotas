package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.databinding.ActivityToReceiveBinding;

public class ToReceiveActivity extends AppCompatActivity {
    private ActivityToReceiveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityToReceiveBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.button.setOnClickListener((View.OnClickListener) v -> {
            Intent intent= new Intent(ToReceiveActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}