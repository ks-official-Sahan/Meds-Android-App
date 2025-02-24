package com.sahansachintha.meds.helper.data;

import android.util.Log;

import com.sahansachintha.meds.helper.DatabaseHelper;
import com.sahansachintha.meds.model.Cart;
import com.sahansachintha.meds.model.ProductItem;
import com.sahansachintha.meds.model.Product;

import java.util.List;
import java.util.Objects;

public class CartManager {
    private static volatile CartManager instance;
    private Cart cart;

    private CartManager() {
        this.cart = loadCartFromDatabase();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product, int quantity) {
        try {
            cart = new Cart.Builder()
                    .setCartItems(cart.getCartItems())  // ✅ Preserve existing items
                    .addProduct(product, quantity)
                    .build();
            saveCartToDatabase();
        } catch (IllegalArgumentException e) {
            Log.w("CartManager", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void updateQuantity(Product product, int quantity) {
        try {
            cart = new Cart.Builder()
                    .setCartItems(cart.getCartItems())  // ✅ Preserve existing items
                    .updateQuantity(product, quantity)
                    .build();
            saveCartToDatabase();
        } catch (IllegalArgumentException e) {
            Log.w("CartManager", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void removeProduct(String productId) {
        cart = new Cart.Builder()
                .setCartItems(cart.getCartItems())  // ✅ Preserve existing items
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

    public void clearCart() {
        cart.clearCart();
        cart = new Cart.Builder().build();  // ✅ Empty cart
        deleteCartFromDatabase();
    }

    public Cart getCart() {
        return cart;
    }

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
