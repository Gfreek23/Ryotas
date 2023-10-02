package com.example.trialproject3.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.LocusId;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginPageActivity extends AppCompatActivity {



    EditText email,password;
    private FirebaseAuth auth;

    TextView signup;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        signup = findViewById(R.id.textView39);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.Email1);
        password = findViewById(R.id.Password4);


    }

    public void Signin(View view){

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"Enter Email Address!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Enter Password!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length()< 6){
            Toast.makeText(this,"Password too short, enter minimum 6 characters!",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(LoginPageActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginPageActivity.this,MainActivity.class));

                                }else{
                                    Toast.makeText(LoginPageActivity.this, "Access Denied!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


    }


        public void Register(View view) {
            startActivity(new Intent(LoginPageActivity.this, RegisterActivity.class));
        }


}
