package com.sahansachintha.meds.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private final List<ProductItem> cartItems;

    private Cart(Builder builder) {
        this.cartItems = builder.productItems;
    }

    public List<ProductItem> getCartItems() {
        return cartItems;
    }

    public double calculateTotal() {
        return cartItems.stream().mapToDouble(item -> Double.parseDouble(item.getProduct().getPrice()) * item.getQuantity()).sum();
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public static class Builder {
        private List<ProductItem> productItems = new ArrayList<>();

        // ✅ New method to retain existing cart items
        public Builder setCartItems(List<ProductItem> existingItems) {
            if (existingItems != null) {
                this.productItems = new ArrayList<>(existingItems);
            }
            return this;
        }

        public Builder addProduct(Product product, int quantity) {
            if (product == null || quantity <= 0) {
                throw new IllegalArgumentException("Invalid product or quantity.");
            }

            boolean productExists = false;
            for (ProductItem item : productItems) {
                if (item.getProduct().getId().equalsIgnoreCase(product.getId())) {
                    int newQuantity = item.getQuantity() + quantity;
                    if (newQuantity > product.getQuantity()) {
                        throw new IllegalArgumentException("Not enough stock available.");
                    }
                    item.setQuantity(newQuantity);
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                productItems.add(new ProductItem(product, quantity));
            }

            return this;
        }

        public Builder removeProduct(String productId) {
            productItems.removeIf(item -> item.getProduct().getId().equalsIgnoreCase(productId));
            return this;
        }

        public Builder updateQuantity(Product product, int quantity) {
            if (product == null || quantity < 0) {
                throw new IllegalArgumentException("Invalid product or quantity.");
            }

            for (ProductItem item : productItems) {
                if (item.getProduct().getId().equalsIgnoreCase(product.getId())) {
                    if (quantity == 0) {
                        productItems.remove(item);
                    } else {
                        if (quantity > product.getQuantity()) {
                            throw new IllegalArgumentException("Not enough stock available.");
                        }
                        item.setQuantity(quantity);
                    }
                    break;
                }
            }
            return this;
        }

        public Cart build() {
            return new Cart(this);
        }
    }

//    public void removeProduct(int productId) {
//        Iterator<CartItem> iterator = cartItems.iterator();
//        while (iterator.hasNext()) {
//            CartItem item = iterator.next();
//            if (item.getProduct().getId() == productId) {
//                iterator.remove();
//                break;
//            }
//        }
//    }
}
