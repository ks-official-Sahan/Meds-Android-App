package com.sahansachintha.meds.helper.data;

import android.util.Log;

import com.sahansachintha.meds.model.Cart;
import com.sahansachintha.meds.model.Product;

public class CartManager {
    private static Cart cartInstance;

    private CartManager() {
        // Private constructor to prevent instantiation
    }

    public static Cart getInstance() {
        if (cartInstance == null) {
            cartInstance = new Cart();
        }
        return cartInstance;
    }

    public static void addProduct(Product product, int quantity) {
        getInstance().addProduct(product, quantity);
        Log.i("MyMedsCart", "Cart: " + getInstance().getCartItems().size());
        Log.i("MyMedsCart", "Cart: " + getInstance().getCartItems().isEmpty());
        Log.i("MyMedsCart", "Cart: " + getInstance().isCartEmpty());
    }

    public static void updateQuantity(Product product, int quantity) {
        getInstance().setProductQuantity(product, quantity);
    }

    public static void removeProduct(int productId) {
        getInstance().removeProduct(productId);
    }

    public static double getTotalPrice() {
        return getInstance().calculateTotalPrice();
    }

    public static boolean isCartEmpty() {
        return getInstance().getCartItems().isEmpty();
    }
}
