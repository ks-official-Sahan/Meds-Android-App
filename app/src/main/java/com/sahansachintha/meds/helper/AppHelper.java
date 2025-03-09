package com.sahansachintha.meds.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

public class AppHelper {

    private static AppHelper instance;
    public static final String SHARED_PREFERENCE_TAG = "MyMedsPrefs";
    public static final String FIREBASE_TOKEN_TAG = "firebase_token";

    private AppHelper() {
    }

    public static synchronized AppHelper getInstance() {
        if (instance == null) {
            instance = new AppHelper();
        }
        return instance;
    }

    public int getThemeColor(Context context, int attribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attribute, typedValue, true);
        return typedValue.data;
    }

    public void setToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIREBASE_TOKEN_TAG, token);
        editor.apply();
    }

    public String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIREBASE_TOKEN_TAG, null);
    }

    public void removeToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("firebase_token").apply();
    }
}
