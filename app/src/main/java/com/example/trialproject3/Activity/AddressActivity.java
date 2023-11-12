package com.example.trialproject3.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.AddressAdapterV3;
import com.example.trialproject3.Helper.SelectListener;
import com.example.trialproject3.Models.AddressModel;
import com.example.trialproject3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements SelectListener {
    private final String TAG = "AddressActivity";
    Button addAddress;
    RecyclerView recyclerView;
    private List<AddressModel> addressModelList;
    private AddressAdapterV3 addressAdapterV3;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Button paymentBtn;
    Toolbar toolbar;
    String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.address_recycler);
        paymentBtn = findViewById(R.id.payment_btn);
        addAddress = findViewById(R.id.add_address_btn);

        List<AddressModel> addressModelList = new ArrayList<>();
        addressAdapterV3 = new AddressAdapterV3(this, addressModelList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(addressAdapterV3);

        firestore.collection("usersAddress").document(auth.getCurrentUser().getUid())
                .collection("Address").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            addressModelList.clear();
                            for (QueryDocumentSnapshot addressSnapshot : value) {
                                AddressModel addressModel = addressSnapshot.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                Log.i(TAG, "onEvent: " + addressModelList);
                            }
                            addressAdapterV3.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "onEvent: " + error.getMessage());
                        }
                    }
                });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, CartActivity.class));

                // Save the selected address permanently using SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedAddress", mAddress);
                editor.apply();
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, AddAddressActivity.class));
            }
        });
    }

    @Override
    public void setAddress(String address) {
        mAddress = address;
    }

    @Override
    public void onItemClicked(AddressModel addressModel) {
        Toast.makeText(this, addressModel.getUserAddress(), Toast.LENGTH_SHORT).show();

        // Set the selected address in the CartActivity
        setAddress(addressModel.getUserAddress());

        // Pass the selected address to CartActivity
        Intent cartIntent = new Intent(AddressActivity.this, CartActivity.class);
        cartIntent.putExtra("selectedAddress", addressModel.getUserAddress());
        startActivity(cartIntent);
    }
}