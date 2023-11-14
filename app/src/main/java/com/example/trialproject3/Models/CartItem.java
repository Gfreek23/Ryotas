package com.example.trialproject3.Models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productName;
    private int quantity;
    private double price;
    private String address;

    public CartItem(String productName, int quantity, double price, String address) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.address = address;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }
}
