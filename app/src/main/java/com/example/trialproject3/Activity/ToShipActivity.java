package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.databinding.ActivityToShipBinding;

public class ToShipActivity extends AppCompatActivity {
    private ActivityToShipBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityToShipBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToShipActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}