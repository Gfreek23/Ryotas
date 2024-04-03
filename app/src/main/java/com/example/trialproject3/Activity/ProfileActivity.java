package com.example.trialproject3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Firebase.FirebaseHelper;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityProfile2Binding;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfile2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityProfile2Binding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        if (FirebaseHelper.currentUser() != null){

            DocumentReference documentReference = FirebaseHelper.getFireStoreInstance()
                    .collection("users").document(FirebaseHelper.currentUserID());

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String getFirstName = documentSnapshot.getString("Fname");
                    String getEmail = documentSnapshot.getString("email");

                    binding.fullNameTextView.setText(getFirstName);
                    binding.emailTextView.setText(getEmail);
                }
            });
        }


        bottom_navigation();

        binding.toShipBtn.setOnClickListener(v -> {
            // Retrieve the cart items from the intent
            List<PopularDomain> cartItems = (List<PopularDomain>) getIntent().getSerializableExtra("cartItems");

            // Start ToShipActivity and pass the cart items
            Intent intent = new Intent(ProfileActivity.this, ToShipActivity.class);
            intent.putExtra("cartItems", (Serializable) cartItems);
            startActivity(intent);
            finish();
        });
        binding.toPayBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProfileActivity.this, ToShipActivity.class);
            startActivity(intent1);
            finish();
        });

        binding.toReceiveBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(ProfileActivity.this, ToReceiveActivity.class);
            startActivity(intent2);
            finish();
        });

        binding.cancelBtn.setOnClickListener(v -> {
            Intent intent3 = new Intent(ProfileActivity.this, ToCancelActivity.class);
            startActivity(intent3);
            finish();
        });

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MainActivity.class)));

        binding.logoutBtn.setOnClickListener(v -> signOutUser());
    }

    private void signOutUser() {
        FirebaseHelper.signOutUser();
        Intent profileAct = new Intent(ProfileActivity.this, LoginActivity.class);
        profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileAct);
        finish();
    }

    private void bottom_navigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

}
