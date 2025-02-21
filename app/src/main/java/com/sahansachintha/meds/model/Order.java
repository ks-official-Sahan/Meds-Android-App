package com.sahansachintha.meds.model;

import com.sahansachintha.meds.helper.data.OrderManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Order implements Serializable {
    private final String orderId;
    private final List<ProductItem> orderItems;
    private final double totalPrice;
    private final double delivery;
    private final String status;
    private final long timestamp;

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.orderItems = builder.orderItems;
        this.totalPrice = builder.totalPrice;
        this.delivery = builder.delivery;
        this.status = builder.status;
        this.timestamp = builder.timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<ProductItem> getOrderItems() {
        return orderItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getDelivery() {
        return delivery;
    }

    public static class Builder {
        private String orderId = OrderManager.generateOrderId();
        private List<ProductItem> orderItems = new ArrayList<>();
        private double totalPrice;
        private String status = "Pending";
        //private long timestamp = System.currentTimeMillis();
        private long timestamp = Calendar.getInstance().getTimeInMillis();
        private double delivery = 0;

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setDelivery(double delivery) {
            this.delivery = delivery;
            return this;
        }

        public Builder setOrderItems(List<ProductItem> orderItems) {
            this.orderItems = new ArrayList<>(orderItems);
            return this;
        }

        public Builder calculateTotalPrice() {
            this.totalPrice = 0;
            for (ProductItem item : orderItems) {
                this.totalPrice += Double.parseDouble(item.getProduct().getPrice()) * item.getQuantity();
            }
            this.totalPrice += delivery;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Order build() {
            calculateTotalPrice();
            return new Order(this);
        }
    }
}
