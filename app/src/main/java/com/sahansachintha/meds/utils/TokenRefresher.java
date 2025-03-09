package com.sahansachintha.meds.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.network.ApiService;

public class TokenRefresher {
    private static final long INTERVAL = 59 * 60 * 1000; // 1 hour in milliseconds
    public final String TAG = "MyMedsTokenRefresher";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ApiService.fetchToken((token) -> AppHelper.getInstance().setToken(MyMeds.getInstance().getApplicationContext(), token));
            Log.i(TAG, "Token refreshed");

            handler.postDelayed(this, INTERVAL); // Schedule next execution
        }
    };

    public  TokenRefresher() {
        ApiService.fetchToken((token) -> AppHelper.getInstance().setToken(MyMeds.getInstance().getApplicationContext(), token));
    }

    public void start() {
        handler.postDelayed(runnable, INTERVAL); // Start execution
    }

    public void stop() {
        handler.removeCallbacks(runnable); // Stop execution
    }
}
