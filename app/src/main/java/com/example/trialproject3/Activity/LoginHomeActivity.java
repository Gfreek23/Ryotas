package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;

    public class LoginHomeActivity extends AppCompatActivity {
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_home);

        }
        public void login(View view){
          startActivity(new Intent(LoginHomeActivity.this, LoginActivity.class));
        }


        public void singup(View view){

            startActivity(new Intent(LoginHomeActivity.this,RegisterActivity.class));

        }


    }
