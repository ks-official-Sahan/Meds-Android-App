package com.sahansachintha.meds.network;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiService {
    private static final String DOMAIN = "https://2fae-2402-d000-8128-197c-6452-e649-aa9-6840.ngrok-free.app";
    private static final String BASE_URL = DOMAIN + "/api";
    private final OkHttpClient client;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static MediaType getTypeJSON() {
        return JSON;
    }

    public ApiService() {
        client = new OkHttpClient();
    }

    public void syncUser(String token, Callback callback) {
        String url = BASE_URL + "/auth/sync";

        RequestBody body = RequestBody.create("", JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void verifyToken(String token, Callback callback) {
        String url = BASE_URL + "/auth/verify-token";

        RequestBody body = RequestBody.create("", JSON); // the request body is empty since we send the token in the header.

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getProfile(String token, Callback callback) {
        String url = BASE_URL + "/auth/profile";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void updateProfile(String token, String jsonBody, Callback callback) {
        String url = BASE_URL + "/auth/profile";
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void fetchToken(TokenCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String token = task.getResult().getToken();
                    callback.handleToken(token);
                } else {
                    callback.handleToken(null);
                }
            });
        } else {
            callback.handleToken(null);
        }
    }

    public static String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }

    public interface TokenCallback {

        void handleToken(String token);
    }
}
