package com.example.trialproject3.Helper;

import com.example.trialproject3.Models.AddressModel;

public interface SelectListener {
    void setAddress(String address);

    void onItemClicked(AddressModel addressModel);
}
