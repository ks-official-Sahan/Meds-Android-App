package com.sahansachintha.meds.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;
    private static final String PREFS_NAME = "MyMedsPermissionPrefs";

    /* Permission Memory */
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static boolean isPermissionRequestedBefore(Context context, String permission) {
        return getPrefs(context).getBoolean(permission, false);
    }

    private static void markPermissionRequested(Context context, String permission) {
        getPrefs(context).edit().putBoolean(permission, true).apply();
    }
    /* Permission Memory */


    /* General */
    public static boolean checkRuntimePermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean requestPermission(@NonNull Context context, @NonNull String permission) {
        if (checkRuntimePermission(context, permission)) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 0);
            markPermissionRequested(context, permission);
        }
        return checkRuntimePermission(context, permission);
    }
    /* General */

    /* Bluetooth */
    public static boolean getBluetoothPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        return checkRuntimePermission(context, Manifest.permission.BLUETOOTH);
    }
    /* Bluetooth */

    /* Location */
    public static boolean getLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        return checkRuntimePermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean getLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        return checkRuntimePermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    /* Location */


    /* Notification */
    public static boolean getNotificationPermission(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
                return checkRuntimePermission(context, Manifest.permission.POST_NOTIFICATIONS);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.POST_NOTIFICATIONS"}, 0);
            }
        }
        return checkRuntimePermission(context, "android.permission.POST_NOTIFICATIONS");
    }
    /* Notification */

    /* Alarm */
    public static void requestExactAlarmPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
    /* Alarm */

    /* Overlay */
    public static void requestOverlayPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity) && !isPermissionRequestedBefore(activity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, 101);
                markPermissionRequested(activity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            }
        }
    }
    /* Overlay */

    /* Battery */
    public static void requestBatteryOptimizations(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(context.getPackageName()) && !isPermissionRequestedBefore(context, Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            context.startActivity(intent);
            markPermissionRequested(context, Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }
    }
    /* Battery */
}
