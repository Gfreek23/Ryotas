package com.example.trialproject3.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.CartListAdapter;
import com.example.trialproject3.Helper.ChangeNumberItemsListener;
import com.example.trialproject3.Helper.ManagementCart;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.R;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.Serializable;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private TextView totalFeeTxt, taxTxt, deliveryTxt, totalTxt, emptyTxt;
    private double tax;
    private ScrollView scrollView;
    private ImageView backBtn;

    private ImageView addBtn;
    private Button btnfinal;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        addBtn = findViewById(R.id.imageView13);
        backBtn = findViewById(R.id.backBtn);
        btnfinal = findViewById(R.id.Order);
        builder = new AlertDialog.Builder(this);

        bottom_navigation();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedAddress")) {
            String selectedAddress = intent.getStringExtra("selectedAddress");
            TextView addressTextView = findViewById(R.id.textView30);
            addressTextView.setText(selectedAddress);
        }

        btnfinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Are you sure?")
                        .setMessage("Do you want to purchase these items?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = getIntent();
                                String selectedAddress = "";
                                if (intent != null && intent.hasExtra("selectedAddress")) {
                                    selectedAddress = intent.getStringExtra("selectedAddress");
                                }

                                Intent toShipIntent = new Intent(CartActivity.this, ToShipActivity.class);
                                toShipIntent.putExtra("selectedAddress", selectedAddress);
                                toShipIntent.putExtra("cartItems", (Serializable) managementCart.getListCart());
                                startActivity(toShipIntent);

                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        ImageView toProfileBtn = findViewById(R.id.imageView8);
        toProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PopularDomain> cartItems = managementCart.getListCart();
                Intent profileIntent = new Intent(CartActivity.this, ProfileActivity.class);
                profileIntent.putExtra("cartItems", (Serializable) cartItems);
                startActivity(profileIntent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Are you sure?")
                        .setMessage("Do you want to leave your cart?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                startActivity(new Intent(CartActivity.this, MainActivity.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, AddressActivity.class));
            }
        });

        managementCart = new ManagementCart(this);

        initView();
        initList();
        calculateCart();
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        });

        recyclerView.setAdapter(adapter);
        if (managementCart.getListCart().isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managementCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managementCart.getTotalFee() * 100) / 100;

        totalFeeTxt.setText("₱" + itemTotal);
        taxTxt.setText("₱" + tax);
        deliveryTxt.setText("₱" + delivery);
        totalTxt.setText("₱" + total);
    }

    private void initView() {
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        recyclerView = findViewById(R.id.view3);
        scrollView = findViewById(R.id.scrollView3);
        emptyTxt = findViewById(R.id.emptyTxt);

        ImageView backBtn = findViewById(R.id.backBtn);
        BottomAppBar bottomAppBar = findViewById(R.id.Appbar);

        if (managementCart.getListCart().isEmpty()) {
            backBtn.setVisibility(View.VISIBLE);
            bottomAppBar.setVisibility(View.GONE);
        } else {
            backBtn.setVisibility(View.VISIBLE);
            bottomAppBar.setVisibility(View.VISIBLE);
        }
    }

    private void bottom_navigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}