package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;

public class RegisterActivity extends AppCompatActivity {

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

   public void Signin(View view){
       startActivity(new Intent(RegisterActivity.this,LoginPageActivity.class));

   }

   public void Signup(View view){
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
   }

}
