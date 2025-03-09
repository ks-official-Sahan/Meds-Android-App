package com.sahansachintha.meds.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sahansachintha.meds.activity.home.AlarmActivity;
import com.sahansachintha.meds.helper.LocationHelper;
import com.sahansachintha.meds.helper.NotificationHelper;
import com.sahansachintha.meds.helper.data.ReminderManager;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "MyMedsAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm triggered!");

        if (intent == null || intent.getAction() == null) {
            Log.e(TAG, "Received null or empty intent action");
            return;
        }
        if (!(context.getPackageName() + ".ALARM_TRIGGERED").equals(intent.getAction())) {
            Log.w(TAG, "Unknown action received: " + intent.getAction());
            return;
        }

        int reminderId = intent.getIntExtra(ReminderManager.REMINDER_ID_EXTRA, -1);
        long scheduledTime = intent.getLongExtra(ReminderManager.SCHEDULED_TIME_EXTRA, 0);

        Calendar now = Calendar.getInstance();
        if (scheduledTime > 0 && now.getTimeInMillis() > scheduledTime + (2 * 60 * 1000)) { // Missed by 2+ min
            Log.w(TAG, "Missed alarm detected!");
            NotificationHelper.showMissedAlarmNotification(context, reminderId);
            autoSnooze(context, reminderId);
            return;
        }

        // 🔥 Use a Full-Screen Intent (for Android 10+ alarm behavior)
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(ReminderManager.REMINDER_ID_EXTRA, reminderId);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                reminderId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 🔥 Show High-Priority Notification with Full-Screen Intent
        NotificationHelper.showFullScreenAlarmNotification(context, fullScreenPendingIntent);
        context.startActivity(alarmIntent);
    }

    private void autoSnooze(Context context, int reminderId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10); // Auto-snooze for 10 minutes
        AlarmScheduler.scheduleReminder(context, reminderId, calendar);

        // 🔥 Get Location If Alarm is Missed
        LocationHelper.getInstance().getCurrentLocation(context);
    }

}
