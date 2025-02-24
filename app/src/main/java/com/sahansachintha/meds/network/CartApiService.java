// CartApiService.java
package com.sahansachintha.meds.network;

import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class CartApiService {
    // Base URL points to your NestJS cart endpoint (adjust as needed)
    private static final String BASE_URL = ApiService.getBaseUrl() + "/cart";
    public static final MediaType JSON_MEDIA_TYPE = ApiService.getTypeJSON();

    private final OkHttpClient client;
    private final Gson gson;

    private String token;

    public CartApiService() {
        client = new OkHttpClient();
        gson = new Gson();
        ApiService.getToken(token1 -> token = token1);
    }

    // GET /cart: Fetch the current cart for the user.
    public void getCart(Callback callback) {
        String url = BASE_URL;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // POST /cart/add: Add a product to the cart.
    public void addProduct(String productId, int quantity, Callback callback) {
        String url = BASE_URL + "/add";
        CartProductPayload payload = new CartProductPayload(productId, quantity);
        String json = gson.toJson(payload);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // PUT /cart/update: Update the quantity of a product in the cart.
    public void updateProduct(String productId, int quantity, Callback callback) {
        String url = BASE_URL + "/update";
        CartProductPayload payload = new CartProductPayload(productId, quantity);
        String json = gson.toJson(payload);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // DELETE /cart/remove: Remove a product from the cart.
    public void removeProduct(String productId, Callback callback) {
        String url = BASE_URL + "/remove";
        // Sending payload to identify the product.
        CartProductPayload payload = new CartProductPayload(productId, 0);
        String json = gson.toJson(payload);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // DELETE /cart/clear: Remove all products from the cart.
    public void clearCart(Callback callback) {
        String url = BASE_URL + "/clear";
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // Inner class representing the API payload for cart operations.
    private static class CartProductPayload {
        private String productId;
        private int quantity;

        public CartProductPayload(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
