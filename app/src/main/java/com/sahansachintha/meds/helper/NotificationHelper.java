package com.sahansachintha.meds.helper;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.HomeActivity;
import com.sahansachintha.meds.helper.data.ReminderManager;

public class NotificationHelper {

    private static final String DEFAULT_CHANNEL_ID = "GENERAL_CHANNEL";
    private static final String ALARM_CHANNEL_ID = "ALARM_CHANNEL";
    private static final String MISSED_ALARM_CHANNEL_ID = "MISSED_ALARM_CHANNEL";

    public static void showFullScreenAlarmNotification(Context context, PendingIntent fullScreenPendingIntent) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        createNotificationChannel(context,
                "Reminder Notifications",
                ALARM_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH,
                "MyMeds Medication Reminder Notification Channel",
                NotificationCompat.VISIBILITY_PUBLIC,
                audioAttributes,
                alarmSound);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Medication Reminder \uD83D\uDC8A\uD83E\uDE7A")
                .setContentText("\uD83D\uDD25 Wake up! It's time to take your medicine.")
                //.setPriority(NotificationCompat.PRIORITY_HIGH)  // 🔥 High priority ensures immediate delivery
                .setPriority(NotificationCompat.PRIORITY_MAX)  // 🔥 Ensures top-priority notification
                .setCategory(NotificationCompat.CATEGORY_ALARM) // 🔥 Mark this as an alarm 💊🩺
                //.setAutoCancel(true)
                .setAutoCancel(false)  // 🔥 Prevents dismissal by swipe
                //.setOngoing(true) // 🔥 Keeps notification active
                .setSound(alarmSound)
                .setVibrate(new long[]{0, 1000, 500, 1000}) // Vibrate pattern
                .setFullScreenIntent(fullScreenPendingIntent, true) // 🔥 Full-screen intent for alarms
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    public static void showMissedAlarmNotification(Context context, int reminderId) {
        createNotificationChannel(context, "Missed Reminder", MISSED_ALARM_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH, "Missed Reminder Notification Channel");

        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(ReminderManager.REMINDER_ID_EXTRA, reminderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MISSED_ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Missed Alarm")
                .setContentText("You missed an alarm. It has been snoozed for 10 minutes.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.getNotificationPermission(context);
        }
        notificationManager.notify(reminderId, builder.build());
    }

    /* Notification Channel Creation */
    private static void createNotificationChannel(Context context) {
        createNotificationChannel(context, "General Notifications", DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT, "App Notification");
    }

    private static void createNotificationChannel(Context context, CharSequence name, String CHANNEL_ID) {
        createNotificationChannel(context, name, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT, null);
    }

    private static void createNotificationChannel(Context context, CharSequence name, String CHANNEL_ID, int importance) {
        createNotificationChannel(context, name, CHANNEL_ID, importance, null);
    }

    private static void createNotificationChannel(Context context, CharSequence name, String CHANNEL_ID, int importance, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            if (description != null) {
                channel.setDescription(description);
            }

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static void createNotificationChannel(Context context, CharSequence name, String CHANNEL_ID, int importance, String description, int lockscreenVisibility, AudioAttributes audioAttributes, Uri sound) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            if (description != null) {
                channel.setDescription(description);
            }
            if (lockscreenVisibility != -1) {
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            }
            if (audioAttributes != null && sound != null) {
                channel.setSound(sound, audioAttributes);
            }

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    /* Notification Channel Creation */

}
