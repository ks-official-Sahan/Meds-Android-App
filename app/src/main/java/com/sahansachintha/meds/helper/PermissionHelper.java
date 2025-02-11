package com.sahansachintha.meds.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static boolean checkRuntimePermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean getRuntimePermission(@NonNull Context context, @NonNull String permission) {
        if (checkRuntimePermission(context, permission)) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 0);
        }
        return checkRuntimePermission(context, permission);
    }

    public static void getNotificationRuntimePermission(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.POST_NOTIFICATIONS"}, 0);
            }
        }
    }

    public static void getBatteryOptimizationRuntimePermission(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, 0);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"}, 0);
            }
        }
    }

    /* violates google play policy on ignore battery optimization */
    @SuppressLint("QueryPermissionsNeeded")
    public static void checkBatteryOptimization(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));

                // Check if there's an Activity to handle the Intent
                PackageManager packageManager = context.getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    context.startActivity(intent);
                }
            }
        }
    }


    public static void getAlarmRuntimePermission(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 0);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android. permission. SCHEDULE_EXACT_ALARM"}, 0);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestExactAlarmPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

}
