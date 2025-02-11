package com.sahansachintha.meds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sahansachintha.meds.activity.HomeActivity;
import com.sahansachintha.meds.model.Reminder;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    //@SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MyMedsBroadcast", "Working");

        if (intent == null || intent.getAction() == null) {
            Log.e("MyMedsBroadcast", "Received null or empty intent action");
            return;
        }

        /* Check if the action is correct */
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            List<Reminder> reminders = getSavedReminders(context); // Load reminders from database

            for (Reminder reminder : reminders) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(reminder.getTimeInMillis());
                AlarmScheduler.scheduleReminder(context, reminder.getId(), calendar);
            }
            Log.i("MyMedsBroadcast", "Reminders added after boot");
        } else {
            Log.e("App28Log", "Received intent with unexpected action: ${intent.getAction()}");
        }
    }

    private List<Reminder> getSavedReminders(Context context) {
        ArrayList<Reminder> reminderList = new ArrayList<>();

        /* Load reminders from SharedPreferences, Room Database, or SQLite */
        //reminderList.add(new Reminder());

        return reminderList;
    }
}
