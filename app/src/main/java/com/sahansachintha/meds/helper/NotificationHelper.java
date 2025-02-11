package com.sahansachintha.meds.helper;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.AlarmActivity;
import com.sahansachintha.meds.activity.HomeActivity;
import com.sahansachintha.meds.activity.SplashActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "missed_alarm_channel";

    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void showMissedAlarmNotification(Context context, int reminderId) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("reminderId", reminderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Missed Alarm")
                .setContentText("You missed an alarm. It has been snoozed for 10 minutes.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.getNotificationRuntimePermission(context);
        }
        notificationManager.notify(reminderId, builder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Missed Alarm Notifications";
            String description = "Notifies when an alarm is missed";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
