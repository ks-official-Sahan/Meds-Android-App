package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderManager {
    private static ReminderManager reminderManager;

    private ReminderManager() {
    }

    public static ReminderManager getInstance() {
        if (reminderManager == null) {
            reminderManager = new ReminderManager();
        }
        return reminderManager;
    }

    public ArrayList<Reminder> getSampleData() {
        ArrayList<Reminder> reminderList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        calendar.set(2025, Calendar.FEBRUARY, 12, 8, 30); // 12th Feb, 8:30 AM
        reminderList.add(new Reminder(1, "Reminder 1", calendar.getTimeInMillis(), "Finish Reminder Development"));

        calendar.set(2025, Calendar.FEBRUARY, 12, 10, 30); // 12th Feb, 8:30 AM
        reminderList.add(new Reminder(2, "Reminder 2", calendar.getTimeInMillis(), "Finish Medication Development"));

        calendar.set(2025, Calendar.FEBRUARY, 13, 8, 30); // 13th Feb, 8:30 AM
        reminderList.add(new Reminder(3, "Reminder 3", calendar.getTimeInMillis(), "Finish Store Home Development"));

        calendar.set(2025, Calendar.FEBRUARY, 14, 8, 30); // 14th Feb, 8:30 AM
        reminderList.add(new Reminder(4, "Reminder 4", calendar.getTimeInMillis(), "Finish Store Product View Development"));

        return reminderList;
    }

}
