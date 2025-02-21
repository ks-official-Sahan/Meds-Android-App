package com.sahansachintha.meds.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cart implements Serializable {
    private List<CartItem> cartItems;

    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addProduct(Product product, int quantity) {
        if (quantity <= 0) return;

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > product.getQuantity()) {
                    Log.w("MyMedsCart", "Not enough stock!");
                    return; // Prevent adding more than available stock
                }
                item.setQuantity(newQuantity);
                return;
            }
        }

        if (quantity > product.getQuantity()) {
            Log.w("MyMedsCart", "Not enough stock!");
            return;
        }

        cartItems.add(new CartItem(product, quantity));
    }

    public void removeProduct(int productId) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId() == productId) {
                iterator.remove();
                break;
            }
        }
    }

    public void setProductQuantity(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            try {
                total += Double.parseDouble(item.getProduct().getPrice()) * item.getQuantity();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public void saveToDatabase() {
        // TODO Implement database saving logic
    }

    public void loadFromDatabase() {
        // TODO Implement database loading logic
    }

    public void deleteFromDatabase() {
        // TODO Implement database deletion logic
    }

    public void updateInDatabase() {
        // TODO Implement database update logic
    }
}
