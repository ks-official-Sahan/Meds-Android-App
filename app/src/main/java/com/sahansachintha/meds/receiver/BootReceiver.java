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

    public static final String TAG = "MyMedsBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BootReceiver triggered");
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Initialize ReminderManager to load reminders and schedule alarms.
            ReminderManager.getInstance();
            Log.i(TAG, "ReminderManager initialized on boot");
        } else {
            Log.e(TAG, "Unexpected intent: " + (intent != null ? intent.getAction() : "null"));
        }
    }
}
