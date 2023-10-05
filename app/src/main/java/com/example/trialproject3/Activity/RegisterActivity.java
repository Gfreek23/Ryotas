package com.example.trialproject3.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialproject3.R;
import com.example.trialproject3.UserDetails.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password, Repassword, Bday, Phone;
    private FirebaseAuth auth;

    private ProgressBar progressBar;

    private RadioGroup Gender;
    private RadioButton GenderSelect;

    FirebaseFirestore fstore;

    private static final String TAG = "RegisterActivity";

    String userID;

    SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressbar);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.Email);
        Bday = findViewById(R.id.Birthday);
        Phone = findViewById(R.id.Phone);
        password = findViewById(R.id.Password3);
        Repassword = findViewById(R.id.RePassword);

        Gender = findViewById(R.id.Gender);
        Gender.clearCheck();

        sharedPreferences = getSharedPreferences("OnBoardActivity", MODE_PRIVATE);

        boolean isFirstTIme = sharedPreferences.getBoolean("firstTime", true);

        if (isFirstTIme) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(RegisterActivity.this, OnBoardActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void Signin(View view) {

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userBday = Bday.getText().toString();
        String userGender;
        String userPhone = Phone.getText().toString();
        String userPassword = password.getText().toString();
        String userRepassword = Repassword.getText().toString();

        int GenderSelected = Gender.getCheckedRadioButtonId();
        GenderSelect = findViewById(GenderSelected);


        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please enter your full name!", Toast.LENGTH_SHORT).show();
            name.setError("Full Name is required");
            name.requestFocus();

        } else if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please enter email address!", Toast.LENGTH_SHORT).show();
            email.setError("Email is required");
            email.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Please Re-Enter your email address!", Toast.LENGTH_SHORT).show();
            email.setError("Valid email is required");
            email.requestFocus();
        } else if (TextUtils.isEmpty(userBday)) {
            Toast.makeText(this, "Please enter Date of Birth!", Toast.LENGTH_SHORT).show();
            Bday.setError("Date of birth is required");
            Bday.requestFocus();
        } else if (Gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender!", Toast.LENGTH_SHORT).show();
            GenderSelect.setError("Gender is required");
            GenderSelect.requestFocus();
        } else if (TextUtils.isEmpty(userPhone)) {
            Toast.makeText(this, "Please enter your mobile number!", Toast.LENGTH_SHORT).show();
            Phone.setError("Mobile No. is required");
            Phone.requestFocus();
        } else if (userPhone.length() < 11) {
            Toast.makeText(this, "Please Re-enter your mobile no.!", Toast.LENGTH_SHORT).show();
            Phone.setError("Mobile No. should be 11 digits");
            Phone.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();
            password.setError("Password is required");
            password.requestFocus();
        } else if (userPassword.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 digits!", Toast.LENGTH_SHORT).show();
            password.setError("Password is too weak!");
            password.requestFocus();
        } else if (TextUtils.isEmpty(userRepassword)) {
            Toast.makeText(this, "Please confirm your password!", Toast.LENGTH_SHORT).show();
            Repassword.setError("Password confirmation is required");
            Repassword.requestFocus();
        } else if (!userRepassword.equals(userPassword)) {
            Toast.makeText(this, "Please enter same password!", Toast.LENGTH_SHORT).show();
            Repassword.setError("Password confirmation is required");
            Repassword.requestFocus();

            password.clearComposingText();
            Repassword.clearComposingText();
        } else {
            userGender = GenderSelect.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            registerUser(userName, userEmail, userBday, userGender, userPhone, userPassword);
        }


    }

    private void registerUser(String userName, String userEmail, String userBday, String userGender, String userPhone, String userPassword) {

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Successfully Registered. Please verify your email",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            firebaseUser.sendEmailVerification();

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(userName, userBday, userGender, userPhone);

                            userID = auth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Fname", userName);
                            user.put("email", userEmail);
                            user.put("Bday", userBday);
                            user.put("Phone", userPhone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registered failed. Please try again",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


    public void Login (View view){
        startActivity(new Intent(RegisterActivity.this, LoginPageActivity.class));
    }

}

