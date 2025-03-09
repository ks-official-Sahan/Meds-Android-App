package com.sahansachintha.meds.utils;

import android.os.Handler;
import android.os.Looper;

import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.network.ApiService;

public class TokenRefresher {
    private static final long INTERVAL = 60 * 60 * 1000; // 1 hour in milliseconds
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ApiService.fetchToken((token) -> AppHelper.getInstance().setToken(MyMeds.getInstance().getApplicationContext(), token));

            handler.postDelayed(this, INTERVAL); // Schedule next execution
        }
    };

    public void start() {
        handler.postDelayed(runnable, INTERVAL); // Start execution
    }

    public void stop() {
        handler.removeCallbacks(runnable); // Stop execution
    }
}
