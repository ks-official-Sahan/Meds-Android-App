package com.sahansachintha.meds.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sahansachintha.meds.helper.AppHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AuthService {
    private static final String TAG = "MyMedsAuthService";
    private final FirebaseAuth auth;
    private final ApiService apiService;
    private final Context context;

    public AuthService(Context context) {
        this.auth = FirebaseAuth.getInstance();
        this.apiService = new ApiService();
        this.context = context;
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);

        void onFailure(String errorMessage);
    }

    public interface TokenCallback {
        void onTokenReceived(String token);

        void onFailure(String errorMessage);
    }

    public void signUp(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        FirebaseUser user = task.getResult().getUser();
                        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                            if (tokenTask.isSuccessful() && tokenTask.getResult() != null) {
                                String token = tokenTask.getResult().getToken();
                                saveToken(token);
                                // Sync the user record with the NestJS backend
                                apiService.syncUser(token, new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e(TAG, "Sync failed: " + e.getMessage());
                                        callback.onFailure("Sync failed: " + e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            // Optionally, you can parse the JSON response using Gson if needed.
                                            assert response.body() != null;
                                            Log.d(TAG, "User sync successful: " + response.body().string());
                                            callback.onSuccess(user);
                                        } else {
                                            callback.onFailure("Sync failed: HTTP " + response.code());
                                        }
                                        response.close();
                                    }
                                });
                            } else {
                                callback.onFailure(tokenTask.getException() != null ? tokenTask.getException().getMessage() : "Token retrieval failed");
                            }
                        });
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Signup failed");
                    }
                });
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        FirebaseUser user = task.getResult().getUser();
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Login failed");
                    }
                });
    }

    public void logout() {
        auth.signOut();
        AppHelper.getInstance().removeToken(context);
        AppHelper.getInstance().removeUser(context);
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show();
    }

    public void getToken(FirebaseUser user, TokenCallback callback) {
        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getToken();
                saveToken(token);
                callback.onTokenReceived(token);
                //sendTokenToBackend(token);
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Token retrieval failed");
            }
        });
    }

    private void saveToken(String token) {
        AppHelper.getInstance().setToken(context, token);
    }

    private void sendTokenToBackend(String token) {
        apiService.verifyToken(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error sending token to backend: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token verified by backend");
                } else {
                    Log.e(TAG, "Backend rejected token: HTTP " + response.code());
                }
                response.close();
            }
        });
    }
}
