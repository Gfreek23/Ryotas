package com.example.trialproject3.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.OrderedItemsAdapter;
import com.example.trialproject3.Models.CartItem;
import com.example.trialproject3.Helper.Utils;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.databinding.ActivityToShipBinding;
import com.example.trialproject3.R;

import java.util.List;

public class ToShipActivity extends AppCompatActivity {
    private ActivityToShipBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToShipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView orderedItemsRecyclerView = findViewById(R.id.orderedItemsRecyclerView);
        Button btnBackToCart = findViewById(R.id.button);
        TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);
        TextView totalCostTextView = findViewById(R.id.totalPriceTextView);

        // Retrieve the ordered items, user address, and total price from the intent
        Intent intent = getIntent();
        if (intent != null) {
            List<PopularDomain> popularDomainList = (List<PopularDomain>) intent.getSerializableExtra("cartItems");
            String selectedAddress = intent.getStringExtra("selectedAddress");
            double totalPrice = intent.getDoubleExtra("totalPrice", 0.0);

            // Convert PopularDomain list to CartItem list
            List<CartItem> orderedItems = Utils.filterCartItems(popularDomainList, selectedAddress);

            // Calculate total cost
            double totalCost = calculateTotalCost(orderedItems);

            // Display user address, total price, and total cost
            totalPriceTextView.setText("Total Price: ₱" + totalPrice);
            totalCostTextView.setText("Total Cost: ₱" + totalCost);

            // Set up the RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            orderedItemsRecyclerView.setLayoutManager(layoutManager);
            OrderedItemsAdapter adapter = new OrderedItemsAdapter(orderedItems, selectedAddress, totalPrice);
            orderedItemsRecyclerView.setAdapter(adapter);

            // Save ordered items
            saveOrderedItems(orderedItems, selectedAddress, totalCost);
        }

        btnBackToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CartActivity when the button is clicked
                startActivity(new Intent(ToShipActivity.this, CartActivity.class));
            }
        });

        // Add Cancel Order functionality
        Button btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ToShipActivity.this);
                builder.setTitle("Cancel Order");
                builder.setMessage("Are you sure you want to cancel this order?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear saved ordered items
                        clearOrderedItems();
                        // Return to CartActivity
                        startActivity(new Intent(ToShipActivity.this, CartActivity.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog if "No" is clicked
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Calculate total cost of all items
    private double calculateTotalCost(List<CartItem> orderedItems) {
        double totalCost = 0;
        for (CartItem item : orderedItems) {
            totalCost += item.getPrice() * item.getQuantity();
        }
        return totalCost;
    }

    // Save ordered items using SharedPreferences
    private void saveOrderedItems(List<CartItem> orderedItems, String address, double totalCost) {
        sharedPreferences = getSharedPreferences("OrderedItems", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Serialize ordered items to JSON using Gson
        String orderedItemsJson = CartItem.listToJson(orderedItems);

        editor.putString("orderedItems", orderedItemsJson);
        editor.putString("address", address);
        editor.putFloat("totalCost", (float) totalCost);
        editor.apply();
    }

    // Retrieve ordered items from SharedPreferences
    private List<CartItem> getOrderedItems() {
        sharedPreferences = getSharedPreferences("OrderedItems", Context.MODE_PRIVATE);
        String orderedItemsJson = sharedPreferences.getString("orderedItems", "");
        return CartItem.listFromJson(orderedItemsJson);
    }

    // Clear saved ordered items
    private void clearOrderedItems() {
        sharedPreferences = getSharedPreferences("OrderedItems", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}