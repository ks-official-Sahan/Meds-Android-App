package com.sahansachintha.meds.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sahansachintha.meds.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleReminder(Context context, int id, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(context.getPackageName() + ".ALARM_TRIGGERED");
        intent.putExtra("reminderId", id);
        intent.putExtra("scheduledTime", calendar.getTimeInMillis());

        int flags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, intent, flags
        );

        if (alarmManager != null) {
            try {
                if (canScheduleExactAlarm(context)) {
                    //checkBatteryOptimization(context);

                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    Toast.makeText(context, "Alarm at " + calendar.getTime() + " scheduled successfully", Toast.LENGTH_SHORT).show();
                    Log.d("MyMedsAlarmScheduler", "Exact alarm at " + calendar.getTime() + " scheduled successfully.");
                } else {
                    Log.w("MyMedsAlarmScheduler", "Exact alarm scheduling not permitted.");
                    Toast.makeText(context, "Exact alarm scheduling not permitted.", Toast.LENGTH_SHORT).show();
                }
            } catch (SecurityException e) {
                Toast.makeText(context, "SecurityException: Cannot schedule exact alarms", Toast.LENGTH_SHORT).show();
                Log.e("MyMedsAlarmScheduler", "SecurityException: Cannot schedule exact alarms", e);
            }
        }
    }

    public static boolean canScheduleExactAlarm(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                boolean canSchedule = alarmManager.canScheduleExactAlarms();
                Log.d("MyMedsAlarmUtils", "Can Schedule Exact Alarms: " + canSchedule);
                if (!canSchedule) {
                    requestExactAlarmPermission(context);
                }
                return canSchedule;
            }
        }
        return true; // Below API 31, exact alarms are allowed by default
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestExactAlarmPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

//    @SuppressLint("QueryPermissionsNeeded")
//    public static void checkBatteryOptimization(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            String packageName = context.getPackageName();
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intent.setData(Uri.parse("package:" + packageName));
//
//                // Check if there's an Activity to handle the Intent
//                PackageManager packageManager = context.getPackageManager();
//                if (intent.resolveActivity(packageManager) != null) {
//                    context.startActivity(intent);
//                }
//            }
//        }
//    }

    public static void cancelReminder(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(pendingIntent);
    }
}
