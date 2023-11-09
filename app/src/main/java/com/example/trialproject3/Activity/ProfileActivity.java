package com.example.trialproject3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityProfile2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfile2Binding binding;

    TextView Fname,Email;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityProfile2Binding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        Fname = findViewById(R.id.profname);
        Email = findViewById(R.id.emailpfp);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userId = fauth.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("users").document(userId);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()){
                String getFirstName = documentSnapshot.getString("Fname");
                String getEmail = documentSnapshot.getString("email");

                Fname.setText(getFirstName);
                Email.setText(getEmail);
            }
        });

        bottom_navigation();

        binding.shipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ToShipActivity.class);
                startActivity(intent);
                finish();

            }
        });
        binding.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ProfileActivity.this, ToShipActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        binding.recieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ProfileActivity.this, ToReceiveActivity.class);
                startActivity(intent2);
                finish();
            }
        });

        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ProfileActivity.this, ToCancelActivity.class);
                startActivity(intent3);
                finish();
            }
        });

        binding.backBtnPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        binding.Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fauth.signOut();
                signOutUser();

            }
        });
        binding.backBtnPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    private void signOutUser() {
        fauth.signOut();
        Intent profileAct = new Intent(ProfileActivity.this,LoginPageActivity.class);
        profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileAct);
        finish();
    }

    private void bottom_navigation() {
        LinearLayout homeBtn=findViewById(R.id.homeBtn);
        LinearLayout cartBtn=findViewById(R.id.cartBtn);
        LinearLayout profileBtn=findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

}
