// CartManager.java
package com.sahansachintha.meds.helper.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
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
    private static volatile CartManager instance;
    private Cart cart;
    private CartApiService cartApiService;
    private final Gson gson;

    private CartManager() {
        this.cart = loadCartFromDatabase();
        cartApiService = new CartApiService();
        gson = new Gson();
        syncCartFromServer(); // Optionally sync on startup.
    }

    private CartManager(Context context) {
        this.cart = loadCartFromDatabase();
        cartApiService = new CartApiService(context);
        gson = new Gson();
        syncCartFromServer(); // Optionally sync on startup.
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        instance.cartApiService = new CartApiService(context);
        return instance;
    }

    // Add a product locally and sync with the server.
    public void addProduct(Product product, int quantity) {
        try {
            cart = new Cart.Builder()
                    .setCartItems(cart.getCartItems()) // Preserve existing items.
                    .addProduct(product, quantity)
                    .build();
            //saveCartToDatabase();
            // Call backend API.
            cartApiService.addProduct(product.getId(), quantity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("CartManager", "Error adding product to server: " + e.getMessage());
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("CartManager", "Product added to server successfully.");
                    response.close();
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w("CartManager", Objects.requireNonNull(e.getMessage()));
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
            cartApiService.updateProduct(product.getId(), quantity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("CartManager", "Error updating product on server: " + e.getMessage());
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("CartManager", "Product updated on server successfully.");
                    response.close();
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w("CartManager", Objects.requireNonNull(e.getMessage()));
        }
    }

    // Remove a product locally and sync with the server.
    public void removeProduct(String productId) {
        cart = new Cart.Builder()
                .setCartItems(cart.getCartItems())
                .removeProduct(productId)
                .build();
        saveCartToDatabase();
        cartApiService.removeProduct(productId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("CartManager", "Error removing product on server: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("CartManager", "Product removed on server successfully.");
                response.close();
            }
        });
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
        cartApiService.clearCart(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.e("CartManager", "Error clearing cart on server: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("CartManager", "Cart cleared on server successfully.");
                response.close();
            }
        });
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

    // Sync the local cart with the backend.
    public void syncCartFromServer() {
        cartApiService.getCart(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("CartManager", "Error fetching cart from server: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();
                    Log.i("CartManager", "Fetched cart from server: " + jsonResponse);
                    // Assuming the backend returns a JSON matching your Cart model.
                    Cart serverCart = gson.fromJson(jsonResponse, Cart.class);
                    Log.i("CartManager", "Fetched cart from server: " + serverCart);
                    if (serverCart != null) {
                        cart = serverCart;
                        //saveCartToDatabase();
                        Log.i("CartManager", "Cart synchronized from server.");
                    }
                } else {
                    Log.e("CartManager", "Failed to fetch cart from server, HTTP code: " + response.code());
                }
                response.close();
            }
        });
    }
}
