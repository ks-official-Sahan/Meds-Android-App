package com.sahansachintha.meds;

import android.app.Application;

public class MyMeds extends Application {

    private static MyMeds instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyMeds getInstance() {
        return instance;
    }
}
