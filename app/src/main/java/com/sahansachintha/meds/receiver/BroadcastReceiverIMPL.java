package com.sahansachintha.meds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sahansachintha.meds.activity.home.HomeActivity;

public class BroadcastReceiverIMPL extends BroadcastReceiver {

    public static final String TAG = "MyMedsBroadcast";

    //@SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Working");

        if (intent == null || intent.getAction() == null) {
            Log.e(TAG, "Received null or empty intent action");
            return;
        }

        /* Check if the action is correct */
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean state = intent.getBooleanExtra("state", false);

            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, state ? "Airplane Mode On" : "Airplane Mode Off", Toast.LENGTH_SHORT).show();
            });

            if (state) {
            //    Toast.makeText(context, "State True", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(context, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            //} else {
            //    Toast.makeText(context, "State False", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Received intent with unexpected action: ${intent.getAction()}");
        }
    }
}
