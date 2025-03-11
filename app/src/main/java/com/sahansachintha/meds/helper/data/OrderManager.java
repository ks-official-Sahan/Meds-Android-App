package com.sahansachintha.meds.helper.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sahansachintha.meds.model.Order;
import com.sahansachintha.meds.network.OrderApiService;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class OrderManager {
    private static volatile OrderManager instance;
    private final List<Order> orders;
    private final OrderApiService orderApiService;
    private final Gson gson;

    private OrderManager() {
        this.orders = new ArrayList<>();
        orderApiService = new OrderApiService();
        gson = new Gson();
        loadOrdersFromServer(); // Synchronize orders on startup.
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    // Add a new order locally and on the server.
    public void addOrder(Order order) {
        try {
            addNewOrder(order).get();
        } catch (Exception e) {
            Log.e("OrderManager", "Error adding order: " + e.getMessage());
        }
    }

    public CompletableFuture<Order> addNewOrder(Order order) {
        CompletableFuture<Order> futureOrder = new CompletableFuture<>();

        orderApiService.createOrder(order, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OrderManager", "Error creating order on server: " + e.getMessage());
                futureOrder.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.i("OrderManager", "JSON Response: " + jsonResponse);

                    Order serverOrder = gson.fromJson(jsonResponse, Order.class);
                    if (serverOrder != null) {
                        orders.add(serverOrder);
                        futureOrder.complete(serverOrder);
                    } else {
                        futureOrder.completeExceptionally(new Exception("Failed to parse server response"));
                    }
                } else {
                    futureOrder.completeExceptionally(new Exception("Failed to create order, HTTP code: " + response.code()));
                }
                response.close();
            }
        });

        return futureOrder;
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

    // Update an order's status locally and remotely.
    public void updateOrderStatus(String orderId, String newStatus) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getOrderId().equals(orderId)) {
                Order updatedOrder = new Order.Builder()
                        .setOrderId(order.getOrderId())
                        .setOrderItems(order.getOrderItems())
                        .setDelivery(order.getDelivery())
                        .calculateTotalPrice()
                        .setStatus(newStatus)
                        .setTimestamp(order.getTimestamp())
                        .build();
                orders.remove(i);
                orders.add(updatedOrder);
                updateOrderInDatabase(updatedOrder);
                orderApiService.updateOrderStatus(orderId, newStatus, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("OrderManager", "Error updating order status on server: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.i("OrderManager", "Order status updated on server: " + orderId);
                        response.close();
                    }
                });
                break;
            }
        }
    }

    // Remove an order locally and remotely.
    public void removeOrder(String orderId) {
        orders.removeIf(order -> order.getOrderId().equals(orderId));
        deleteOrderFromDatabase(orderId);
        orderApiService.deleteOrder(orderId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OrderManager", "Error deleting order on server: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("OrderManager", "Order deleted on server: " + orderId);
                response.close();
            }
        });
    }

    // Load orders from your backend server.
    public void loadOrdersFromServer() {
        orderApiService.getOrders(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OrderManager", "Error loading orders from server: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    // Assuming the server returns a JSON array matching your Order model.
                    Log.i("OrderManager", "JSON Response: " + jsonResponse);
                    Order[] serverOrders = gson.fromJson(jsonResponse, Order[].class);
                    orders.clear();
                    if (serverOrders != null) {
                        Collections.addAll(orders, serverOrders);
                    }
                    Log.i("OrderManager", "Orders synchronized from server.");
                } else {
                    Log.e("OrderManager", "Failed to load orders, HTTP code: " + response.code());
                }
                response.close();
            }
        });
    }

    public static String generateOrderId() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long timestamp = calendar.getTimeInMillis() / 10000; // Shorter Unix timestamp
        int randomPart = (int) (Math.random() * 9000) + 1001; // 4-digit random number
        return String.format(Locale.US, "%d-%d", timestamp, randomPart);
    }

    // Local persistence methods (to be implemented as needed).
    private void saveOrderToDatabase(Order order) {
        Log.d("OrderManager", "Saving order locally: " + order.getOrderId());
        // TODO: Implement local DB storage if needed.
    }

    private void updateOrderInDatabase(Order order) {
        Log.d("OrderManager", "Updating order locally: " + order.getOrderId());
        // TODO: Implement local DB update if needed.
    }

    private void deleteOrderFromDatabase(String orderId) {
        Log.d("OrderManager", "Deleting order locally: " + orderId);
        // TODO: Implement local DB deletion if needed.
    }
}
