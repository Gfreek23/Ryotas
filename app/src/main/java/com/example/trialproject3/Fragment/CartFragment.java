package com.example.trialproject3.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trialproject3.Activity.AddressActivity;
import com.example.trialproject3.Activity.CartActivity;
import com.example.trialproject3.Activity.MainActivity;
import com.example.trialproject3.Activity.ToShipActivity;
import com.example.trialproject3.Adapter.CartListAdapter;
import com.example.trialproject3.Helper.ChangeNumberItemsListener;
import com.example.trialproject3.Helper.ManagementCart;
import com.example.trialproject3.Map.MapboxMapActivity;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.FragmentCartBinding;

import java.io.Serializable;

public class CartFragment extends Fragment implements MainActivity.OnBackPressedListener {
    private static final String TAG = "CartFragment";
    private FragmentCartBinding binding;
    private Context context;
    private RecyclerView.Adapter adapter;
    private ManagementCart managementCart;
    private double tax;
    private AlertDialog.Builder builder;
    private Intent intent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        context = requireContext();
        builder = new AlertDialog.Builder(context);
        binding.emptyTxt.setVisibility(View.GONE);

        intent = requireActivity().getIntent();
        if (intent != null && intent.hasExtra("selectedAddress")) {
            String selectedAddress = intent.getStringExtra("selectedAddress");
            binding.addressTextView.setText(selectedAddress);
        }

        binding.orderNowBtn.setOnClickListener(v -> builder.setTitle("Are you sure?")
                .setMessage("Do you want to purchase these items?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent1 = requireActivity().getIntent();
                    String selectedAddress = "";
                    if (intent1 != null && intent1.hasExtra("selectedAddress")) {
                        selectedAddress = intent1.getStringExtra("selectedAddress");
                    }

                    Intent toShipIntent = new Intent(context, ToShipActivity.class);
                    toShipIntent.putExtra("selectedAddress", selectedAddress);
                    toShipIntent.putExtra("cartItems", (Serializable) managementCart.getListCart());
                    startActivity(toShipIntent);

                    requireActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
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
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.cancel();
                    backToHomeFragment();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show());

        binding.addAddressBtn.setOnClickListener(v ->
                startActivity(new Intent(context, AddressActivity.class)));

        managementCart = new ManagementCart(context);

        initView();
        initList();
        calculateCart();

        return binding.getRoot();

    }
    @Override
    public boolean onBackPressed() {
        backToHomeFragment();
        return true;
    }
    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.itemsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(managementCart.getListCart(), context, new ChangeNumberItemsListener() {
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

    private void backToHomeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new HomeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
        binding.totalTxt.setText("₱" + total);
    }

    private void initView() {
        if (managementCart.getListCart().isEmpty()) {
            binding.backBtn.setVisibility(View.VISIBLE);
        } else {
            binding.backBtn.setVisibility(View.VISIBLE);
        }
    }


}