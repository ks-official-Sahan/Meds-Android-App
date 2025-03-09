package com.sahansachintha.meds.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.data.ReminderManager;
import com.sahansachintha.meds.model.Reminder;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.Calendar;
import java.util.List;

public class ReminderReschedulerService extends IntentService {

    public ReminderReschedulerService() {
        super("ReminderReschedulerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("ReminderRescheduler", "Service started for rescheduling reminders");
        // Initialize ReminderManager and get cached reminders.
        ReminderManager reminderManager = ReminderManager.getInstance();
        List<Reminder> reminders = reminderManager.getAllReminders();
        Context appContext = MyMeds.getInstance().getApplicationContext();
        for (Reminder reminder : reminders) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(reminder.getTimeInMillis());
            AlarmScheduler.scheduleReminder(appContext, reminder.getId(), calendar);
        }
        Log.i("ReminderRescheduler", "All reminders have been rescheduled.");
    }
}