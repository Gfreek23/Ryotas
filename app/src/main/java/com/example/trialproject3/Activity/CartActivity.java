package com.example.trialproject3.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.CartListAdapter;
import com.example.trialproject3.Fragment.CartFragment;
import com.example.trialproject3.Fragment.HomeFragment;
import com.example.trialproject3.Fragment.ProfileFragment;
import com.example.trialproject3.Helper.ChangeNumberItemsListener;
import com.example.trialproject3.Helper.ManagementCart;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Map.MapboxMapActivity;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityCartBinding;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagementCart managementCart;
    private double tax;
    private AlertDialog.Builder builder;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        builder = new AlertDialog.Builder(this);
        initializeBottomNavBar();

         intent = getIntent();
        if (intent != null && intent.hasExtra("selectedAddress")) {
            String selectedAddress = intent.getStringExtra("selectedAddress");
            TextView addressTextView = findViewById(R.id.textView30);
            addressTextView.setText(selectedAddress);
        }

        binding.orderNowBtn.setOnClickListener(v -> builder.setTitle("Are you sure?")
                .setMessage("Do you want to purchase these items?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent1 = getIntent();
                    String selectedAddress = "";
                    if (intent1 != null && intent1.hasExtra("selectedAddress")) {
                        selectedAddress = intent1.getStringExtra("selectedAddress");
                    }

                    Intent toShipIntent = new Intent(CartActivity.this, ToShipActivity.class);
                    toShipIntent.putExtra("selectedAddress", selectedAddress);
                    toShipIntent.putExtra("cartItems", (Serializable) managementCart.getListCart());
                    startActivity(toShipIntent);

                    finish();
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show());


//        toProfileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<PopularDomain> cartItems = managementCart.getListCart();
//                Intent profileIntent = new Intent(CartActivity.this, ProfileActivity.class);
//                profileIntent.putExtra("cartItems", (Serializable) cartItems);
//                startActivity(profileIntent);
//            }
//        });

        binding.backBtn.setOnClickListener(v -> builder.setTitle("Are you sure?")
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
                .show());

        binding.addAddressBtn.setOnClickListener(v ->
                startActivity(new Intent(CartActivity.this, AddressActivity.class)));

        managementCart = new ManagementCart(this);

        initView();
        initList();
        calculateCart();
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.itemsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        });

        binding.itemsRecyclerView.setAdapter(adapter);
        if (managementCart.getListCart().isEmpty()) {
          binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managementCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managementCart.getTotalFee() * 100) / 100;

        binding.totalFeeTxt.setText("₱" + itemTotal);
        binding.taxTxt.setText("₱" + tax);
        binding.deliveryTxt.setText("₱" + delivery);
        binding. totalTxt.setText("₱" + total);
    }

    private void initView() {
        if (managementCart.getListCart().isEmpty()) {
            binding.backBtn.setVisibility(View.VISIBLE);
        } else {
            binding.backBtn.setVisibility(View.VISIBLE);
        }
    }


    private void initializeBottomNavBar() {
        binding.bottomNavBar.setSelectedItemId(R.id.navHomeServices);
        binding.bottomNavBar.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navHomeServices) {
                 intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.navExplore) {
                 intent = new Intent(CartActivity.this, MapboxMapActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.navCart) {
//                showFragment(new CartFragment());
            } else if (item.getItemId() == R.id.navProfile) {
//                showFragment(new ProfileFragment());
            }

            return true;
        });
    }
}