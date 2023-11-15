package com.example.trialproject3.Models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

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

    // Convert CartItem to JSON using Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Create CartItem from JSON using Gson
    public static CartItem fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CartItem.class);
    }

    // Convert CartItem to JSONObject
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productName", productName);
            jsonObject.put("quantity", quantity);
            jsonObject.put("price", price);
            jsonObject.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Create CartItem from JSONObject
    public static CartItem fromJsonObject(JSONObject jsonObject) {
        try {
            String productName = jsonObject.getString("productName");
            int quantity = jsonObject.getInt("quantity");
            double price = jsonObject.getDouble("price");
            String address = jsonObject.getString("address");
            return new CartItem(productName, quantity, price, address);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert list of CartItems to JSON using Gson
    public static String listToJson(List<CartItem> cartItems) {
        Gson gson = new Gson();
        return gson.toJson(cartItems);
    }

    // Create list of CartItems from JSON using Gson
    public static List<CartItem> listFromJson(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CartItem>>(){}.getType();
        return gson.fromJson(json, listType);
    }
}