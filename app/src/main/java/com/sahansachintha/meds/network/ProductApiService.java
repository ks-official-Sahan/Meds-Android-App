package com.sahansachintha.meds.network;

import com.google.gson.Gson;
import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.model.Product;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ProductApiService {
    private static final String BASE_URL = ApiService.getBaseUrl();
    public static final MediaType JSON_MEDIA_TYPE = ApiService.getTypeJSON();
    private static String token;

    private final OkHttpClient client;
    private final Gson gson;

    public ProductApiService() {
        client = new OkHttpClient();
        gson = new Gson();
        token = AppHelper.getInstance().getToken(MyMeds.getInstance().getApplicationContext());
    }

    // GET /products - load all products
    public void getAllProducts(Callback callback) {
        String url = BASE_URL + "/products";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // POST /products - save a new product
    public void createProduct(Product product, Callback callback) {
        String url = BASE_URL + "/products";
        // Convert the product to JSON.
        String json = gson.toJson(product);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // PUT /products/:id - update a product
    public void updateProduct(Product product, Callback callback) {
        // Since MongoDB uses string IDs, we convert the product id to string.
        String productId = String.valueOf(product.getId());
        String url = BASE_URL + "/products/" + productId;
        String json = gson.toJson(product);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
