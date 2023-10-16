package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.databinding.ActivityToPayBinding;

public class ToPayActivity extends AppCompatActivity {
    private ActivityToPayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityToPayBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToPayActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}