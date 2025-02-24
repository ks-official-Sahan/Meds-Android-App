package com.sahansachintha.meds.network;

import com.google.gson.Gson;
import com.sahansachintha.meds.model.Category;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CategoryApiService {
    private static final String BASE_URL = ApiService.getBaseUrl();
    public static final MediaType JSON_MEDIA_TYPE = ApiService.getTypeJSON();

    private final OkHttpClient client;
    private final Gson gson;

    public CategoryApiService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    // GET /categories – load all categories.
    public void getAllCategories(Callback callback) {
        String url = BASE_URL + "/categories";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    // POST /categories – create a new category.
    public void createCategory(Category category, Callback callback) {
        String url = BASE_URL + "/categories";
        String json = gson.toJson(category);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // PUT /categories/:id – update a category.
    public void updateCategory(Category category, Callback callback) {
        String url = BASE_URL + "/categories/" + category.getId();
        String json = gson.toJson(category);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
