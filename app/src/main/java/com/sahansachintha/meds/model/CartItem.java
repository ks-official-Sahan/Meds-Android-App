package com.sahansachintha.meds.model;

import android.util.Log;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    public double getTotalPrice() {
        try {
            return Double.parseDouble(product.getPrice()) * quantity;
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            Log.e("MyMedsCartItem", "Error parsing price: " + e.getMessage());
            return 0.0;
        }
    }
}
