package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText name,email,password,Repassword;
    private FirebaseAuth auth;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance();


        name = findViewById(R.id.Name);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password3);
        Repassword = findViewById(R.id.RePassword);


    }

    public void Signin (View view){

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userRepassword = Repassword.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,"Enter Name!",Toast.LENGTH_SHORT).show();
            return;
        }

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
        if(TextUtils.isEmpty(userRepassword)){
            Toast.makeText(this,"Re type your password!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length()< 6){
            Toast.makeText(this,"Password too short, enter minimum 6 characters!",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Successfully Register", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,LoginPageActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Registration Failed"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void Login(View view){
        startActivity(new Intent(RegisterActivity.this,LoginPageActivity.class));
    }


}
