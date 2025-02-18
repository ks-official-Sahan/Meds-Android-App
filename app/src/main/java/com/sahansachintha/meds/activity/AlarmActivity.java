package com.sahansachintha.meds.activity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.LocationHelper;
import com.sahansachintha.meds.helper.NotificationHelper;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private int reminderId;
    private final Handler missedAlarmHandler = new Handler();
    private boolean isAlarmHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        /* Make activity appear on lock screen */
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alarm_main), (v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return insets;
        });

        reminderId = getIntent().getIntExtra("reminderId", -1);

        TextView timeText = findViewById(R.id.alarm_time);
        timeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime()));

        // Play alarm sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
        ringtone.play();

        // Handle missed alarms
        missedAlarmHandler.postDelayed(this::handleMissedAlarm, 60000); // 60-second timeout

        Button confirmButton = findViewById(R.id.btn_confirm);
        Button snoozeButton = findViewById(R.id.btn_snooze);

        confirmButton.setOnClickListener(v -> {
            isAlarmHandled = true;
            stopAlarm();
        });
        snoozeButton.setOnClickListener(v -> {
            isAlarmHandled = true;
            snoozeAlarm();
        });
    }

    private void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        missedAlarmHandler.removeCallbacksAndMessages(null);

        // 🔥 Get Location on Dismiss
        LocationHelper.getInstance().getCurrentLocation(this);

        finish();
    }

    private void snoozeAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10); // Snooze for 10 minutes

        AlarmScheduler.scheduleReminder(this, reminderId, calendar);
        missedAlarmHandler.removeCallbacksAndMessages(null);
        finish();
    }

    private void handleMissedAlarm() {
        if (!isAlarmHandled) {
            NotificationHelper.showMissedAlarmNotification(this, reminderId);
            snoozeAlarm(); // Auto-snooze the alarm
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        missedAlarmHandler.removeCallbacksAndMessages(null);
    }
}