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

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    Context context;
    private List<AddressModel> addressModelList;
    SelectedAddress selectedAddress;

    private RadioButton selectedRadioBtn;

    public AddressAdapter(Context context, List<AddressModel> addressModelList) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.selectedAddress = selectedAddress;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = (LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int clickedPosition = position;

        holder.address.setText(" , " + addressModelList.get(position).getUserAddress());
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (AddressModel address : addressModelList) {
                    address.setSelected(false);
                }
                addressModelList.get(clickedPosition).setSelected(true);

                if (selectedRadioBtn != null) {
                    selectedRadioBtn.setChecked(false);
                }
                selectedRadioBtn = (RadioButton) v;
                selectedRadioBtn.setChecked(true);
                selectedAddress.setAddress(addressModelList.get(clickedPosition).getUserAddress());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }

    public interface SelectedAddress {
        void setAddress(String address);
    }
}
