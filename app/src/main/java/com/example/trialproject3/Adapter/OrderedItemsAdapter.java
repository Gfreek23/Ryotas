package com.example.trialproject3.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Models.CartItem;
import com.example.trialproject3.R;

import java.util.List;

public class OrderedItemsAdapter extends RecyclerView.Adapter<OrderedItemsAdapter.ViewHolder> {
    private final List<CartItem> orderedItems;
    private final String selectedAddress;
    private final double totalPrice;

    public OrderedItemsAdapter(List<CartItem> orderedItems, String selectedAddress, double totalPrice) {
        this.orderedItems = orderedItems;
        this.selectedAddress = selectedAddress;
        this.totalPrice = totalPrice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordered_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = orderedItems.get(position);

        // Set the data to the views
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.quantityTextView.setText("Quantity: " + cartItem.getQuantity());
        holder.priceTextView.setText("Price: ₱" + cartItem.getPrice());
        holder.addressTextView.setText("Shipping Address: " + selectedAddress);
    }

    @Override
    public int getItemCount() {
        return orderedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        TextView addressTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
        }
    }
}
