package com.sahansachintha.meds.network;

import android.util.Log;

import com.google.gson.Gson;
import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.AppHelper;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CartApiService {
    private static final String BASE_URL = ApiService.getBaseUrl() + "/cart";
    public static final MediaType JSON_MEDIA_TYPE = ApiService.getTypeJSON();

    private static final String TAG = "MyMedsCartApiService";

    private final OkHttpClient client;
    private final Gson gson;
    private final String token;

    public CartApiService() {
        client = new OkHttpClient();
        gson = new Gson();
        token = AppHelper.getInstance().getToken(MyMeds.getInstance().getApplicationContext());
        if (token == null) {
            Log.e(TAG, "No token available in CartApiService constructor");
        }
    }

    public void getCart(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

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

    public void removeProduct(String productId, Callback callback) {
        String url = BASE_URL + "/remove";
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

    public void clearCart(Callback callback) {
        String url = BASE_URL + "/clear";
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private static class CartProductPayload {
        private String productId;
        private int quantity;

        public CartProductPayload(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
