package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.model.Reminder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ReminderManager {

    private static volatile ReminderManager instance;
    private final List<Reminder> reminders;

    private ReminderManager() {
        this.reminders = new ArrayList<>();
        initializeSampleData();
    }

    public static ReminderManager getInstance() {
        if (instance == null) {
            synchronized (ReminderManager.class) {
                if (instance == null) {
                    instance = new ReminderManager();
                }
            }
        }
        return instance;
    }

    private void initializeSampleData() {
        addReminder(1, "Morning Medicine", 2025, Calendar.FEBRUARY, 12, 8, 30, "Take on an empty stomach.");
        addReminder(2, "Afternoon Medication", 2025, Calendar.FEBRUARY, 12, 14, 0, "Take with food.");
        addReminder(3, "Evening Dose", 2025, Calendar.FEBRUARY, 13, 20, 0, "Avoid caffeine after this dose.");
        addReminder(4, "Night Supplement", 2025, Calendar.FEBRUARY, 14, 22, 0, "Drink plenty of water.");
    }

    public boolean addReminder(int id, String title, int year, int month, int day, int hour, int minute, String notes) {
        if (getReminderById(id).isPresent()) {
            return false; // Prevent duplicate IDs
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return reminders.add(new Reminder(id, title, calendar.getTimeInMillis(), notes));
    }

    public boolean updateReminder(int id, String title, long timeInMillis, String notes) {
        Optional<Reminder> optionalReminder = getReminderById(id);
        if (optionalReminder.isPresent()) {
            Reminder reminder = optionalReminder.get();
            reminder.setTitle(title);
            reminder.setTimeInMillis(timeInMillis);
            reminder.setNotes(notes);
            return true;
        }
        return false; // Reminder not found
    }

    public boolean removeReminder(int id) {
        return reminders.removeIf(reminder -> reminder.getId() == id);
    }

    public List<Reminder> getAllReminders() {
        //return Collections.unmodifiableList(reminders);
        return  new ArrayList<>(reminders);
    }

    public List<Reminder> getRemindersForDate(LocalDate date) {
        return reminders.stream()
                .filter(reminder -> convertToLocalDate(reminder.getTimeInMillis()).equals(date))
                .collect(Collectors.toList());
    }

    private Optional<Reminder> getReminderById(int id) {
        return reminders.stream().filter(reminder -> reminder.getId() == id).findFirst();
    }

    private LocalDate convertToLocalDate(long timeInMillis) {
        return Instant.ofEpochMilli(timeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
