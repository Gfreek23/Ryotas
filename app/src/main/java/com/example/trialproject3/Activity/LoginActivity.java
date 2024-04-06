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

import com.example.trialproject3.Firebase.FirebaseHelper;
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
    private AlertDialog.Builder builder;
    private Intent intent;

    //check niya ang user kung naka login in na para kung naka login na siya kasa diritso na siya sa profile
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseHelper.currentUser() != null) {
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

        binding.progressbarLog.setVisibility(View.GONE);
        builder = new AlertDialog.Builder(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageView imageViewShowHide = findViewById(R.id.imageview_show_hide);
        imageViewShowHide.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHide.setOnClickListener(v -> {
            if (binding.passwordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                binding.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                imageViewShowHide.setImageResource(R.drawable.ic_hide_pwd);
            } else {
                binding.passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewShowHide.setImageResource(R.drawable.ic_show_pwd);
            }
        });


        binding.loginBtn.setOnClickListener(v -> signInUser());

        binding.signUpTextView.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        binding.forgotPasswordTextView.setOnClickListener(v -> forgotPassword());

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
            binding.progressbarLog.setVisibility(View.VISIBLE);
            loginUser(userEmail, userPassword);
        }
    }

    private void loginUser(String userEmail, String userPassword) {
        FirebaseHelper.getAuth()
                .signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    binding.progressbarLog.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        if (task.getResult().getUser().isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            task.getResult().getUser().sendEmailVerification();
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
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You cannot login without email verification.");

        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //sa email app ni siya mo diritso dili sa atoang app mag open
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void forgotPassword() {
        EditText resetMail = new EditText(LoginActivity.this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(LoginActivity.this);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter your email to received reset link.");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
            //kuhaon ang email niya sa firestore unya mag send og reset password link
            String mail = resetMail.getText().toString();
            FirebaseHelper.getAuth()
                    .sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to sent reset password link" + task.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        passwordResetDialog.setNegativeButton("No", (dialog, which) -> {


        });
        passwordResetDialog.create().show();
    }
}
