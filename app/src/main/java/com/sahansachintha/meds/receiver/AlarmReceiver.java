package com.sahansachintha.meds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sahansachintha.meds.activity.AlarmActivity;
import com.sahansachintha.meds.helper.NotificationHelper;
import com.sahansachintha.meds.model.Reminder;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyMedsBroadcastAlarm", "Working");

        if (intent == null || intent.getAction() == null) {
            Log.e("MyMedsBroadcastAlarm", "Received null or empty intent action");
            return;
        }
        if (!"com.sahansachintha.meds.ALARM_TRIGGERED".equals(intent.getAction())) {
            Log.w("MyMedsBroadcastAlarm", "Unknown action received: " + intent.getAction());
            return;
        }
        Log.i("MyMedsBroadcastAlarm", "Received intent action: " + intent.getAction());

        int reminderId = intent.getIntExtra("reminderId", -1);
        long scheduledTime = intent.getLongExtra("scheduledTime", 0);
        Log.d("MyMedsBroadcastAlarm", "Received Reminder ID: " + reminderId);

        Calendar now = Calendar.getInstance();
        if (scheduledTime > 0 && now.getTimeInMillis() > scheduledTime + (2 * 60 * 1000)) { // Missed by 2+ min
            Log.w("MyMedsBroadcastAlarm", "Missed alarm detected!");
            NotificationHelper.showMissedAlarmNotification(context, reminderId);
            autoSnooze(context, reminderId);
            return;
        }

        // Launch the alarm screen
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra("reminderId", reminderId);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

    private void autoSnooze(Context context, int reminderId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10); // Auto-snooze for 10 minutes
        AlarmScheduler.scheduleReminder(context, reminderId, calendar);
    }

}
