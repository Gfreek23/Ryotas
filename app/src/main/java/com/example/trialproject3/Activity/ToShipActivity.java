package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.OrderedItemsAdapter;
import com.example.trialproject3.Models.CartItem;
import com.example.trialproject3.Helper.Utils; // Import Utils
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.databinding.ActivityToShipBinding;
import com.example.trialproject3.R;

import java.util.List;

public class ToShipActivity extends AppCompatActivity {
    private ActivityToShipBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToShipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView orderedItemsRecyclerView = findViewById(R.id.orderedItemsRecyclerView);
        Button btnBackToCart = findViewById(R.id.button);
        TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);
        TextView totalCostTextView = findViewById(R.id.totalPriceTextView); // Added TextView

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
            totalCostTextView.setText("Total Cost: ₱" + totalCost); // Set total cost TextView

            // Set up the RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            orderedItemsRecyclerView.setLayoutManager(layoutManager);
            OrderedItemsAdapter adapter = new OrderedItemsAdapter(orderedItems, selectedAddress, totalPrice);
            orderedItemsRecyclerView.setAdapter(adapter);
        }

        btnBackToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CartActivity when the button is clicked
                startActivity(new Intent(ToShipActivity.this, CartActivity.class));
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
}