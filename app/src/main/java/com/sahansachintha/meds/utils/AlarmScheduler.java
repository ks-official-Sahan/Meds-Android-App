package com.sahansachintha.meds.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.sahansachintha.meds.helper.PermissionHelper;
import com.sahansachintha.meds.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleReminder(Context context, int id, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PermissionHelper.getNotificationPermission(context);
        PermissionHelper.getLocationPermission(context);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(context.getPackageName() + ".ALARM_TRIGGERED");
        alarmIntent.putExtra("reminderId", id);
        alarmIntent.putExtra("scheduledTime", calendar.getTimeInMillis());
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, alarmIntent, flags
        );

        if (alarmManager != null) {
            try {
                if (canScheduleExactAlarm(context)) {
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
                    PermissionHelper.requestExactAlarmPermission(context);
                }
                return canSchedule;
            }
        }
        return true; // Below API 31, exact alarms are allowed by default
    }

    public static void cancelReminder(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(pendingIntent);
    }
}
