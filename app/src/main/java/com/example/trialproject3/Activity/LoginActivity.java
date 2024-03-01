package com.example.trialproject3.Activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    AlertDialog.Builder builder;
    private String userID;
    ProgressBar progressBar;

    //check niya ang user kung naka login in na para kung naka login na siya kasa diritso na siya sa profile
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "Your already logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            Toast.makeText(LoginActivity.this, "You can login now!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageView imageViewShowHide = findViewById(R.id.imageview_show_hide);
        imageViewShowHide.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.passwordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    binding.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    imageViewShowHide.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    binding.passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHide.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbarLog);
        builder = new AlertDialog.Builder(this);

        binding.loginBtn.setOnClickListener(v -> signInUser());

    }

    public void signInUser() {

        final String userEmail = binding.emailEditText.getText().toString().trim();
        final String userPassword = binding.passwordEditText.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            binding.emailEditText.setError("Email is required");
            binding.emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Please re-enter your email!", Toast.LENGTH_SHORT).show();
            binding.emailEditText.setError("Valid email is required");
            binding.emailEditText.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            binding.passwordEditText.setError("Password is required");
            binding.passwordEditText.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loginUser(userEmail, userPassword);
        }
    }

    private void loginUser(String userEmail, String userPassword) {
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, task -> {

            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();

                if (firebaseUser.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    firebaseUser.sendEmailVerification();
                    showAlertDialog();
                }

            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    binding.emailEditText.setError("Invalid credentials, Kindly check and re-enter.");
                    binding.emailEditText.requestFocus();
                } catch (FirebaseAuthInvalidUserException e) {
                    binding.emailEditText.setError("User does not exist or is no longer valid, Please ");
                    binding.emailEditText.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, "" + e.getMessage());
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void Forgotpass(View view) {
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
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Failed to sent reset password link" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
            //close niya ang message

        });
        passwordResetDialog.create().show();

    }


}
