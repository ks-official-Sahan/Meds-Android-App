package com.sahansachintha.meds.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.model.User;
import com.sahansachintha.meds.model.dto.UpdateProfileRequest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileService {
    private static final String TAG = "MyMedsProfileService";
    private final ApiService apiService;
    private final Context context;
    private final String token;

    public ProfileService(Context context) {
        this.context = context;
        this.apiService = new ApiService();
        token = AppHelper.getInstance().getToken(context);
    }

    public interface ProfileCallback {
        void onSuccess(User user);

        void onFailure(String errorMessage);
    }

    public void loadProfile(ProfileCallback callback) {
        if (token == null) {
            callback.onFailure("No Firebase token found.");
            return;
        }

        apiService.getProfile(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response successful");
                    String jsonResponse = response.body().string();

                    Gson gson = new Gson();
                    // Convert JSON response to User object
                    User user = gson.fromJson(jsonResponse, User.class);

                    callback.onSuccess(user);
                } else {
                    Log.d(TAG, "Response: "+ response.message());
                    callback.onFailure("Error: " + response.code());
                }
                response.close();
            }
        });
    }

    public interface UpdateProfileCallback {
        void onSuccess(User updatedUser);

        void onFailure(String errorMessage);
    }

    public void updateProfile(String name, String mobile, String address,
                              String city, String country, String profileImage,
                              UpdateProfileCallback callback) {
        if (token == null) {
            callback.onFailure("No Firebase token found.");
            return;
        }

        // Build the update request payload
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setName(name);
        updateRequest.setMobile(mobile);
        updateRequest.setAddress(address);
        updateRequest.setCity(city);
        updateRequest.setCountry(country);
        updateRequest.setProfileImage(profileImage);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(updateRequest);

        apiService.updateProfile(token, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    User updatedUser = gson.fromJson(jsonResponse, User.class);
                    callback.onSuccess(updatedUser);
                } else {
                    callback.onFailure("Error: " + response.code());
                }
                response.close();
            }
        });
    }
}