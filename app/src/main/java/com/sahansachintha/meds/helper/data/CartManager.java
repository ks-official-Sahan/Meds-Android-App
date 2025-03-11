// CartManager.java
package com.sahansachintha.meds.helper.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.DatabaseHelper;
import com.sahansachintha.meds.model.Cart;
import com.sahansachintha.meds.model.Product;
import com.sahansachintha.meds.model.ProductItem;
import com.sahansachintha.meds.network.CartApiService;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CartManager {
    public static final String TAG = "MyMedsCartManager";
    private static volatile CartManager instance;
    private Cart cart;

    private CartManager() {
        // Initialize the local database helper (if not already initialized).
        DatabaseHelper.init(MyMeds.getInstance().getApplicationContext());
        this.cart = loadCartFromDatabase();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Add a product locally and sync with the server.
    public void addProduct(Product product, int quantity) {
        try {
            cart = new Cart.Builder()
                    .setCartItems(cart.getCartItems()) // Preserve existing items.
                    .addProduct(product, quantity)
                    .build();
            // Persist the updated cart locally.
            saveCartToDatabase();
        } catch (IllegalArgumentException e) {
            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    // Update quantity locally and sync with the server.
    public void updateQuantity(Product product, int quantity) {
        try {
            cart = new Cart.Builder()
                    .setCartItems(cart.getCartItems())
                    .updateQuantity(product, quantity)
                    .build();
            saveCartToDatabase();
        } catch (IllegalArgumentException e) {
            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    // Remove a product locally and sync with the server.
    public void removeProduct(String productId) {
        cart = new Cart.Builder()
                .setCartItems(cart.getCartItems())
                .removeProduct(productId)
                .build();
        saveCartToDatabase();
    }

    public List<ProductItem> getCartItems() {
        return cart.getCartItems();
    }

    public double getTotalPrice() {
        return cart.calculateTotal();
    }

    public boolean isCartEmpty() {
        return cart.isCartEmpty();
    }

    // Clear the cart locally and on the backend.
    public void clearCart() {
        cart.clearCart();
        cart = new Cart.Builder().build(); // Reset cart.
        deleteCartFromDatabase();
    }

    public Cart getCart() {
        return cart;
    }

    // Local persistence methods.
    private void saveCartToDatabase() {
        DatabaseHelper.updateCart(cart);
    }

    private Cart loadCartFromDatabase() {
        Cart loadedCart = DatabaseHelper.loadCart();
        return (loadedCart != null) ? loadedCart : new Cart.Builder().build();
    }

    private void deleteCartFromDatabase() {
        DatabaseHelper.deleteCart();
    }

}
