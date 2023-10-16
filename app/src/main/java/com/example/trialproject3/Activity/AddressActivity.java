package com.example.trialproject3.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.AddressAdapter;
import com.example.trialproject3.Adapter.AddressAdapterV3;
import com.example.trialproject3.Adapter.AddressAdapterV3;
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

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {
    private final String TAG = "AddressActivity";
    Button addAddress;
    RecyclerView recyclerView;
    //    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;
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

//        List<AddressModelV2>  addressModelV2List = new ArrayList<>();
        List<AddressModel> addressModelList = new ArrayList<>();
        CollectionReference addressReference = FirebaseFirestore.getInstance()
                .collection("Address");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//         addressAdapter = new AddressAdapter(getApplicationContext(),addressModelList,this);
        AddressAdapterV3 addressAdapterV3 = new AddressAdapterV3(this, addressModelList);
        recyclerView.setAdapter(addressAdapterV3);

        firestore.collection("usersAddress").document(auth.getCurrentUser().getUid())
                .collection("Address").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            addressModelList.clear();
                            for (QueryDocumentSnapshot addressSnapshot : value){
                                AddressModel addressModel = addressSnapshot.toObject(AddressModel.class);
                                addressModelList.add(addressModel);

                                Log.i(TAG, "onEvent: " + addressModelList);

                            }
                            addressAdapterV3.notifyDataSetChanged();
                        }else {
                            Log.e(TAG, "onEvent: " + error.getMessage());
                        }
                    }
                });

//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
//                                AddressModel addressModel = doc.toObject(AddressModel.class);
//                                addressModelList.add(addressModel);
//                                addressAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this,AddAddressActivity.class));
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this,AddAddressActivity.class));
            }
        });
    }


    @Override
    public void setAddress(String address) {

        mAddress = address;

    }
}