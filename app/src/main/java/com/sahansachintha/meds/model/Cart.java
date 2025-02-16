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
        Log.i("MyMedsProduct", "Product: " + product.getTitle() + ", Quantity: " + quantity);
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                Log.i("MyMedsProduct", "CartItems: " + cartItems.size());
                Log.i("MyMedsProduct", "CartItems: " + cartItems.isEmpty());
                Log.i("MyMedsProduct", "CartItems: " + isCartEmpty());
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
        Log.i("MyMedsProduct", "CartItems: " + cartItems.size());
        Log.i("MyMedsProduct", "CartItems: " + cartItems.isEmpty());
        Log.i("MyMedsProduct", "CartItems: " + isCartEmpty());
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
}
