package com.example.trialproject3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Models.AddressModel;

import com.example.trialproject3.R;

import java.util.List;

public class AddressAdapterV3 extends RecyclerView.Adapter<AddressAdapterV3.AddressAdapterViewHolder> {
    Context context;
    private List<AddressModel> addressModelList;
    RadioButton selectedRadioBtn;
    SelectedAddress selectedAddress;
    private int defaultCheckedPosition;

    public AddressAdapterV3(Context context, List<AddressModel> addressModelList) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.selectedAddress = selectedAddress;
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

        // Check if the current position is the default position you want to be checked
        boolean isChecked = (position == defaultCheckedPosition);

        holder.radioButton.setChecked(isChecked); // Set radio button checked status

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for (AddressModel address : addressModelList) {
                    address.setSelected(false);
                }
                addressModelList.get(clickedPosition).setSelected(true);

                if (selectedAddress == null) {
                    selectedAddress = new SelectedAddress() {
                        @Override
                        public void setAddress(String address) {
                            selectedAddress.setAddress(addressModelList.get(clickedPosition).getUserAddress());
                        }
                    }; // Initialize selectedAddress if it's null
                }

                selectedRadioBtn.setChecked(false);
                selectedRadioBtn = (RadioButton) v;
                selectedRadioBtn.setChecked(true);

                // Now that selectedAddress is initialized, you can set the address
                selectedAddress.setAddress(addressModelList.get(clickedPosition).getUserAddress());
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
    public interface SelectedAddress {
        void setAddress(String address);
    }
}