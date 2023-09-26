package com.example.trialproject3.Activity;

import android.content.Intent;
import android.content.LocusId;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;

public class LoginPageActivity extends AppCompatActivity {

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
    }

    public void Signup(View view){
        startActivity(new Intent(LoginPageActivity.this,RegisterActivity.class));
    }

    public void Signin (View view){
        startActivity(new Intent(LoginPageActivity.this,MainActivity.class));
    }

}
