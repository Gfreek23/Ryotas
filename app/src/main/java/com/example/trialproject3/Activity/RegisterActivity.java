package com.example.trialproject3.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.Firebase.FirebaseHelper;
import com.example.trialproject3.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private DatePickerDialog picker;
    private Intent intent;
    private String userID;
    private SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        binding.registerBtn.setOnClickListener(v -> checkFields());

        binding.signInTextView.setOnClickListener(v -> {
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.birthdayEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            picker = new DatePickerDialog(RegisterActivity.this, (view, year1, month1, dayOfMonth)
                    -> binding.birthdayEditText.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();

        });

        sharedPreferences = getSharedPreferences("OnBoardActivity", MODE_PRIVATE);

        boolean isFirstTIme = sharedPreferences.getBoolean("firstTime", true);

        if (isFirstTIme) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();

            Intent intent = new Intent(RegisterActivity.this, OnBoardActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void checkFields() {
        String firstName = binding.firstNameEditText.getText().toString().trim();
        String lastName = binding.lastNameEditText.getText().toString().trim();
        final String userEmail = binding.emailEditText.getText().toString().trim();
        final String userBirthday = binding.birthdayEditText.getText().toString();
        String userGender;
        final String userPhone = binding.phoneEditText.getText().toString().trim();
        final String userPassword = binding.passwordEditText.getText().toString();
        final String userConfirmPassword = binding.confirmPasswordEditText.getText().toString();

        int selectedRadioBtnID = binding.genderRadioGroup.getCheckedRadioButtonId();
        final String fullName = firstName + " " + lastName;

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            binding.firstNameEditText.setError("Full Name is required");
            binding.firstNameEditText.requestFocus();
            binding.lastNameEditText.setError("Full Name is required");
            binding.lastNameEditText.requestFocus();

        } else if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            binding.emailEditText.setError("Email is required");
            binding.emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Please Re-Enter your email address", Toast.LENGTH_SHORT).show();
            binding.emailEditText.setError("Valid email is required");
            binding.emailEditText.requestFocus();
        } else if (TextUtils.isEmpty(userBirthday)) {
            Toast.makeText(this, "Please enter Date of Birt!", Toast.LENGTH_SHORT).show();
            binding.birthdayEditText.setError("Date of birth is required");
            binding.birthdayEditText.requestFocus();
        } else if (binding.genderRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender!", Toast.LENGTH_SHORT).show();
//            genderSelected.setError("Gender is required");
//            genderSelected.requestFocus();
        } else if (TextUtils.isEmpty(userPhone)) {
            Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
            binding.phoneEditText.setError("Mobile No. is required");
            binding.phoneEditText.requestFocus();
        } else if (userPhone.length() < 11) {
            Toast.makeText(this, "Please Re-enter your mobile number", Toast.LENGTH_SHORT).show();
            binding.phoneEditText.setError("Mobile No. should be 11 digits");
            binding.phoneEditText.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();
            binding.passwordEditText.setError("Password is required");
            binding.passwordEditText.requestFocus();
        } else if (userPassword.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
            binding.phoneEditText.setError("Password is too weak");
            binding.passwordEditText.requestFocus();
        } else if (TextUtils.isEmpty(userConfirmPassword)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            binding.confirmPasswordEditText.setError("Password confirmation is required");
            binding.confirmPasswordEditText.requestFocus();
        } else if (!userConfirmPassword.equals(userPassword)) {
            Toast.makeText(this, "Please enter same password", Toast.LENGTH_SHORT).show();
            binding.confirmPasswordEditText.setError("Password confirmation is required");
            binding.confirmPasswordEditText.requestFocus();

            binding.passwordEditText.clearComposingText();
            binding.confirmPasswordEditText.clearComposingText();
        } else {
            RadioButton selectedGender = findViewById(selectedRadioBtnID);
            userGender = selectedGender.getText().toString();
            binding.progressbar.setVisibility(View.VISIBLE);

            registerUser(fullName,
                    userEmail,
                    userBirthday,
                    userGender,
                    userPhone,
                    userPassword);
        }


    }

    private void registerUser(String userName,
                              String userEmail,
                              String userBday,
                              String userGender,
                              String userPhone,
                              String userPassword) {

        FirebaseHelper.getAuth()
                .createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(RegisterActivity.this, "Successfully Registered.",
                                Toast.LENGTH_SHORT).show();
                        DocumentReference documentReference = FirebaseHelper.getFireStoreInstance()
                                .collection("users")
                                .document(task.getResult().getUser().getUid());

                        Map<String, Object> user = new HashMap<>();
                        user.put("Fname", userName);
                        user.put("email", userEmail);
                        user.put("Bday", userBday);
                        user.put("Phone", userPhone);
                        user.put("Gender", userGender);

                        documentReference.set(user)
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: " + e.getMessage());
                                    }
                                });

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            binding.passwordEditText.setError("Your password is too weak. Kindly use a mix of a alphabets, numbers and special characters");
                            binding.passwordEditText.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            binding.emailEditText.setError("Your email is invalid or already in use. Kindly re-enter.");
                            binding.emailEditText.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            binding.emailEditText.setError("User is already registered with this email. Use another email.");
                            binding.emailEditText.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(RegisterActivity.this, "Registered failed. Please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                    binding.progressbar.setVisibility(View.GONE);
                });
    }
}

