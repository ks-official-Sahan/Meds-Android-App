package com.sahansachintha.meds.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.helper.data.ReminderManager;
import com.sahansachintha.meds.model.Reminder;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.Calendar;
import java.util.List;

public class ReminderRescheduleWorker extends Worker {

    public final String TAG = "ReminderRescheduleWorker";

    public ReminderRescheduleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Worker started for rescheduling reminders");
        ReminderManager reminderManager = ReminderManager.getInstance();
        List<Reminder> reminders = reminderManager.getAllReminders();
        Context appContext = MyMeds.getInstance().getApplicationContext();
        for (Reminder reminder : reminders) {
            Calendar calendar = Calendar.getInstance();

            if (calendar.getTimeInMillis() > reminder.getTimeInMillis()) {
                calendar.setTimeInMillis(reminder.getTimeInMillis());
                AlarmScheduler.scheduleReminder(appContext, reminder.getId(), calendar);
            }
        }
        Log.i(TAG, "All reminders have been rescheduled.");
        return Result.success();
    }
}