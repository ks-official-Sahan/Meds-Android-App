package com.sahansachintha.meds.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.model.User;

public class AppHelper {

    public static final String CURRENT_USER_TAG = "current_user";
    public static final String TAG = "MyMedsAppHelper";
    public static final String SHARED_PREFERENCE_TAG = "MyMedsPrefs";
    public static final String FIREBASE_TOKEN_TAG = "firebase_token";

    private static AppHelper instance;

    private final Gson gson;

    private AppHelper() {
        gson = new Gson();
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


    public Context getAppContext() {
        return MyMeds.getInstance().getApplicationContext();
    }


    public void setToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIREBASE_TOKEN_TAG, token);
        editor.apply();
    }

    public void setToken(String token) {
        setToken(getAppContext(), token);
    }

    public String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIREBASE_TOKEN_TAG, null);
    }

    public String getToken() {
        return getToken(MyMeds.getInstance().getApplicationContext());
    }

    public void removeToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(FIREBASE_TOKEN_TAG).apply();
    }


    public void setUser(Context context, String user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d(TAG, "setUser: " + user);

        editor.putString(CURRENT_USER_TAG, user);
        editor.apply();
    }

    public void setUser(String user) {
        setUser(getAppContext(), user);
    }

    public void setUser(Context context, User user) {
        setUser(context, gson.toJson(user));
    }

    public void setUser(User user) {
        setUser(getAppContext(), gson.toJson(user));
    }

    public String getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_USER_TAG, null);
    }

    public String getUser() {
        return getUser(getAppContext());
    }

    public User getUserModel(Context context) {
        String user = getUser(context);
        Log.d(TAG, "getUserModel: " + user);
        return gson.fromJson(user, User.class);
    }

    public User getUserModel() {
        return getUserModel(getAppContext());
    }

    public void removeUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(CURRENT_USER_TAG).apply();
    }

}
