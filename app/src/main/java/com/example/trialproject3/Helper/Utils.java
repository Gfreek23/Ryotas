package com.example.trialproject3.Helper;

import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static CartItem convertToCartItem(PopularDomain popularDomain, String address, int quantity) {
        String productName = popularDomain.getTitle();
        double price = popularDomain.getPrice();

        return new CartItem(productName, quantity, price, address);
    }

    public static List<CartItem> filterCartItems(List<PopularDomain> popularDomains, String address) {
        List<CartItem> cartItemList = new ArrayList<>();

        for (PopularDomain popularDomain : popularDomains) {
            // Assume you have a method to get the quantity for each PopularDomain item
            int quantity = popularDomain.getNumberinCart();

            CartItem cartItem = convertToCartItem(popularDomain, address, quantity);
            if (cartItem != null) {
                cartItemList.add(cartItem);
            }
        }

        return cartItemList;
    }
}