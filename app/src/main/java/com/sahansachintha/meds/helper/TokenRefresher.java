package com.sahansachintha.meds.helper;

import android.os.Handler;
import android.os.Looper;

import com.sahansachintha.meds.network.ApiService;

public class TokenRefresher {
    private static final long INTERVAL = 60 * 60 * 1000; // 1 hour in milliseconds
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Execute API call
            ApiService.getToken((token) -> {
                //AppHelper.getInstance().setToken(SplashActivity.this, token);
            });

            // Schedule next execution
            handler.postDelayed(this, INTERVAL);
        }
    };

    public void start() {
        handler.postDelayed(runnable, INTERVAL); // Start execution
    }

    public void stop() {
        handler.removeCallbacks(runnable); // Stop execution
    }
}
