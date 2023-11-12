package com.example.trialproject3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Helper.SelectListener;
import com.example.trialproject3.Models.AddressModel;

import com.example.trialproject3.R;

import java.util.List;

public class AddressAdapterV3 extends RecyclerView.Adapter<AddressAdapterV3.AddressAdapterViewHolder> {
    Context context;
    private List<AddressModel> addressModelList;
    private SelectListener listener;
    private int checkedPosition = RecyclerView.NO_POSITION; // Initially, no position is checked

    public AddressAdapterV3(Context context, List<AddressModel> addressModelList, SelectListener listener) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressAdapterV3.AddressAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false);
        return new AddressAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapterV3.AddressAdapterViewHolder holder, int position) {
        AddressModel addressModels = addressModelList.get(position);
        final int clickedPosition = position;

        holder.addressTextView.setText(addressModels.getUserAddress());
        holder.radioButton.setChecked(checkedPosition == position); // Set radio button checked status

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update checked position
                checkedPosition = clickedPosition;

                // Notify adapter about the click event
                if (listener != null) {
                    listener.onItemClicked(addressModelList.get(clickedPosition));
                }

                // Notify adapter to update the UI
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public class AddressAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView addressTextView;
        private RadioButton radioButton;

        public AddressAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }
}