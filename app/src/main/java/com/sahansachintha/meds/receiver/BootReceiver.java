package com.sahansachintha.meds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sahansachintha.meds.helper.data.ReminderManager;
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
        //ArrayList<Reminder> reminderList = (ArrayList<Reminder>) ReminderManager.getInstance().getAllReminders();

        // TODO Load Reminders from Database
        /* Load reminders from SharedPreferences, Room Database, or SQLite */

        return reminderList;
    }
}
