package com.example.trialproject3.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.trialproject3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    EditText name,address,city,postalcode,phonenumber;
    Toolbar toolbar;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Button addbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        toolbar=findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.ad_name);
        address = findViewById(R.id.ad_address);
        city = findViewById(R.id.ad_city);
        postalcode = findViewById(R.id.ad_code);
        phonenumber = findViewById(R.id.ad_phone);
        addbtn = findViewById(R.id.ad_add_address);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String userCity = city.getText().toString();
                String userAddress = address.getText().toString();
                String userCode = postalcode.getText().toString();
                String userNumber = phonenumber.getText().toString();

                String final_address = "";

                if (!userName.isEmpty()){
                    final_address+= userName + ", ";
                }
                if (!userCity.isEmpty()){
                    final_address+= userCity + ", ";
                }
                if (!userAddress.isEmpty()){
                    final_address+= userAddress + ", ";
                }
                if (!userCode.isEmpty()){
                    final_address+= userCode + ", ";
                }
                if (!userNumber.isEmpty()){
                    final_address+= userNumber;
                }
                if (!userName.isEmpty()  &&  !userCity.isEmpty()  &&  !userCode.isEmpty()  &&  !userAddress.isEmpty()  &&  !userNumber.isEmpty()){
                    Map<String, String> map = new HashMap<>();
                    map.put("userAddress",final_address);

                    firestore.collection("usersAddress").document(auth.getCurrentUser().getUid())
                            .collection("Address").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(AddAddressActivity.this, "Address Added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(AddAddressActivity.this, "Kindly fill all field", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}