package com.sahansachintha.meds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sahansachintha.meds.service.ReminderReschedulerService;

public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = "MyMedsBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BootReceiver triggered");
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, ReminderReschedulerService.class);
            context.startService(serviceIntent);
            Log.i(TAG, "ReminderReschedulerService started on boot");
        } else {
            Log.e(TAG, "Unexpected intent: " + (intent != null ? intent.getAction() : "null"));
        }
    }
}
