package com.sahansachintha.meds.network;

import com.google.gson.Gson;
import com.sahansachintha.meds.model.Order;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OrderApiService {
    private static final String BASE_URL = ApiService.getBaseUrl() + "/orders";
    public static final MediaType JSON_MEDIA_TYPE = ApiService.getTypeJSON();

    private final OkHttpClient client;
    private final Gson gson;

    private String token;

    public OrderApiService() {
        client = new OkHttpClient();
        gson = new Gson();
        ApiService.getToken(token1 -> {
            token = token1;
        });
    }

    // Create a new order on the server.
    public void createOrder(Order order, Callback callback) {
        String url = BASE_URL;
        String json = gson.toJson(order);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // Retrieve all orders for the user.
    public void getOrders(Callback callback) {
        String url = BASE_URL;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // Update order status.
    public void updateOrderStatus(String orderId, String status, Callback callback) {
        String url = BASE_URL + "/status";
        OrderStatusPayload payload = new OrderStatusPayload(orderId, status);
        String json = gson.toJson(payload);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // Delete an order.
    public void deleteOrder(String orderId, Callback callback) {
        String url = BASE_URL + "/" + orderId;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // Payload for updating order status.
    private static class OrderStatusPayload {
        private String orderId;
        private String status;
        public OrderStatusPayload(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }
    }
}
