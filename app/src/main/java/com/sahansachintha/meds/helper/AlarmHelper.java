package com.sahansachintha.meds.helper;

import android.content.Context;

import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.Calendar;

public class AlarmHelper {

    private static AlarmHelper alarmHelper;

    private AlarmHelper() {
    }

    public static AlarmHelper getInstance() {
        if (alarmHelper == null) {
            alarmHelper = new AlarmHelper();
        }
        return alarmHelper;
    }

    public void scheduleAlarm(Context context, int reminderId) {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MINUTE, 1); // Schedule 1 minutes from now
        calendar.add(Calendar.SECOND, 15); // Schedule 15 seconds from now

        AlarmScheduler.scheduleReminder(context, reminderId, calendar);
    }

    public void scheduleAlarm(Context context, int reminderId, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        AlarmScheduler.scheduleReminder(context, reminderId, calendar);
    }

    public void scheduleReminder(Context context, int id, Calendar calendar) {
        AlarmScheduler.scheduleReminder(context, id, calendar);
    }
}
