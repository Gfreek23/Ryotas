package com.example.trialproject3.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.LocusId;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginPageActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText email,password;
    private FirebaseAuth auth;

    TextView signup,Forgotlink;

    AlertDialog.Builder builder;

    String userID;

    ProgressBar progressBar;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageView imageViewShowHide =findViewById(R.id.imageview_show_hide);
        imageViewShowHide.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    imageViewShowHide.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHide.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        signup = findViewById(R.id.textView39);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.Email1);
        password = findViewById(R.id.Password4);
        Forgotlink = findViewById(R.id.ForgotPwd);
        progressBar = findViewById(R.id.progressbarLog);
        builder = new AlertDialog.Builder(this);



    }

    public void Signin(View view) {

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            email.setError("Email is required");
            email.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Please re-enter your email!", Toast.LENGTH_SHORT).show();
            email.setError("Valid email is required");
            email.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            password.setError("Password is required");
            password.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loginUser(userEmail,userPassword);
        }


    }

    private void loginUser(String userEmail, String userPassword) {
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(LoginPageActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){


                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
                    }else{
                        firebaseUser.sendEmailVerification();
                        showAlertDialog();
                    }

                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        email.setError("Invalid credentials, Kindly check and re-enter.");
                        email.requestFocus();
                    }catch (FirebaseAuthInvalidUserException e){
                        email.setError("User does not exist or is no longer valid, Please ");
                        email.requestFocus();
                    }catch (Exception e) {
                        Log.e(TAG, ""+ e.getMessage());
                        Toast.makeText(LoginPageActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginPageActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You cannot login without email verification.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //sa email app ni siya mo diritso dili sa atoang app mag open
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }


    public void Signup(View view) {
            startActivity(new Intent(LoginPageActivity.this, RegisterActivity.class));
        }

        public void Forgotpass(View view){
        EditText resetMail = new EditText(view.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Reset Password?");
            passwordResetDialog.setMessage("Enter your email to received reset link.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //kuhaon ang email niya sa firestore unya mag send og reset password link
                    String mail = resetMail.getText().toString();
                    auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(LoginPageActivity.this,"Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginPageActivity.this,"Failed to sent reset password link"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //close niya ang message

                }
            });
            passwordResetDialog.create().show();

        }
    //check niya ang user kung naka login in na para kung naka login na siya kasa diritso na siya sa profile
    @Override
   protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null){
            Toast.makeText(LoginPageActivity.this, "Your already logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
        }else {
            Toast.makeText(LoginPageActivity.this, "You can login now!", Toast.LENGTH_SHORT).show();
        }
    }
}
