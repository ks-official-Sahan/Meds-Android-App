package com.sahansachintha.meds.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sahansachintha.meds.MainActivity;

public class MessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyMedsMessagingService";
    public static final String CHANNEL_ID_1 = "channelID1";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.i(TAG, message.getNotification().getTitle());
        Log.i(TAG, message.getNotification().getBody());

        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_1, "Push Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(new MainActivity(), CHANNEL_ID_1)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .build();

        notificationManager.notify(2, notification);
    }
}
