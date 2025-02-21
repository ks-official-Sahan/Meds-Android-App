package com.sahansachintha.meds.helper.data;

import android.util.Log;
import com.sahansachintha.meds.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderManager {
    private static OrderManager instance;
    private final List<Order> orders;

    private OrderManager() {
        this.orders = new ArrayList<>();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addOrder(Order order) {
        orders.add(order);
        saveOrderToDatabase(order);
    }

    public Order getOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                Order updatedOrder = new Order.Builder()
                        .setOrderId(order.getOrderId())
                        .setOrderItems(order.getOrderItems())
                        .setDelivery(order.getDelivery())
                        .calculateTotalPrice()
                        .setStatus(newStatus)
                        .setTimestamp(order.getTimestamp())
                        .build();

                orders.remove(order);
                orders.add(updatedOrder);
                updateOrderInDatabase(updatedOrder);
                break;
            }
        }
    }

    public void removeOrder(String orderId) {
        orders.removeIf(order -> order.getOrderId().equals(orderId));
        deleteOrderFromDatabase(orderId);
    }

    public static String generateOrderId() {
        long timestamp = System.currentTimeMillis() / 10000; // Shorter Unix timestamp
        int randomPart = (int) (Math.random() * 9000) + 1000; // 4-digit random number
        return String.format(Locale.US, "%d-%d", timestamp, randomPart);
    }

    // Database operations (Assuming NestJS with Prisma or Firebase)
    private void saveOrderToDatabase(Order order) {
        Log.d("OrderManager", "Saving order to database: " + order.getOrderId());
        // TODO: Implement actual DB logic (NestJS/Firebase)
    }

    private void updateOrderInDatabase(Order order) {
        Log.d("OrderManager", "Updating order in database: " + order.getOrderId());
        // TODO: Implement actual DB logic
    }

    private void deleteOrderFromDatabase(String orderId) {
        Log.d("OrderManager", "Deleting order from database: " + orderId);
        // TODO: Implement actual DB logic
    }

    public void loadOrdersFromDatabase() {
        Log.d("OrderManager", "Loading orders from database...");
        // TODO: Implement actual DB loading logic
    }
}
