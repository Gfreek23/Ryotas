package com.example.trialproject3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.trialproject3.databinding.ActivityMainBinding;
import com.example.trialproject3.databinding.ActivityProfile2Binding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfile2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityProfile2Binding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.shipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ToShipActivity.class);
                startActivity(intent);
                finish();

            }
        });
        binding.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ProfileActivity.this, ToShipActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        binding.recieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ProfileActivity.this, ToReceiveActivity.class);
                startActivity(intent2);
                finish();
            }
        });

        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ProfileActivity.this, ToCancelActivity.class);
                startActivity(intent3);
                finish();
            }
        });

        binding.backBtnPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

}
