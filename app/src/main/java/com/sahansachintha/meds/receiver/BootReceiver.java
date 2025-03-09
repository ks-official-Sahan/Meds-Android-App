package com.sahansachintha.meds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sahansachintha.meds.worker.ReminderReschedulerWorker;


public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = "MyMedsBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BootReceiver triggered");
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderReschedulerWorker.class).build();
            WorkManager.getInstance(context).enqueue(workRequest);
            Log.i(TAG, "ReminderReschedulerWorker enqueued on boot");
        } else {
            Log.e(TAG, "Unexpected intent: " + (intent != null ? intent.getAction() : "null"));
        }
    }
}
