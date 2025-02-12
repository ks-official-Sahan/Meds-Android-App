package com.sahansachintha.meds.model;

import java.io.Serializable;
import java.util.Calendar;

public class Reminder implements Serializable {

    private int id;
    private String title;
    private long timeInMillis;

    /* Create and Store a Reminder
    Calendar calendar = Calendar.getInstance();
    calendar.set(2025,Calendar.FEBRUARY,12,8,30); // 12th Feb, 8:30 AM

    Reminder reminder = new Reminder(101, "Morning Medicine", calendar.getTimeInMillis());

    // Save to database or shared preferences
    saveReminderToDatabase(reminder);
    */

    /* schedule a reminder:
    Calendar calendar = Calendar.getInstance();
    calendar.set(2025, Calendar.FEBRUARY, 12, 8, 30); // Example: 12th Feb, 8:30 AM
    AlarmScheduler.scheduleReminder(context, 101, calendar);
    */

    /* fallback
    if (!AlarmScheduler.canScheduleExactAlarm(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        AlarmPermissionHelper.requestExactAlarmPermission(context);
    } else {
        AlarmScheduler.scheduleReminder(context, 101, reminder.getCalendar());
    }
    */

    private Calendar calendar;

    private String medicationName;
    private String dosage;
    private String time;
    private String notes;

    public Reminder(int id, String title, long timeInMillis) {
        this.id = id;
        this.title = title;
        this.timeInMillis = timeInMillis;
    }

    public Reminder(int id, String title, long timeInMillis, String notes) {
        this.id = id;
        this.title = title;
        this.timeInMillis = timeInMillis;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setCalendar(Calendar calendar) {
        this.timeInMillis = calendar.getTimeInMillis();
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        if (this.calendar != null) {
            return this.calendar;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }
}
