package com.sahansachintha.meds.helper.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sahansachintha.meds.helper.firestore.FirestoreReminderManager;
import com.sahansachintha.meds.model.Reminder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ReminderManager {

    private static volatile ReminderManager instance;
    private final List<Reminder> reminders;
    private final FirestoreReminderManager firestoreReminderManager;

    private ReminderManager() {
        this.reminders = new ArrayList<>();
        this.firestoreReminderManager = new FirestoreReminderManager();
        initializeSampleData();
        updateReminderListFromFirebase();
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        return addReminder(id, title, calendar.getTimeInMillis(), notes);
    }

    public boolean addReminder(int id, String title, long timeInMillis, String notes) {
        if (getReminderById(id).isPresent()) {
            return false; // Prevent duplicate IDs
        }
        Reminder reminder = new Reminder(id, title, timeInMillis, notes);
        boolean added = reminders.add(reminder);
        if (added) {
            saveReminderToFirestore(reminder);
        }
        return added;
    }

    public void updateReminderListFromFirebase() {
        firestoreReminderManager.loadReminders(firestoreReminders -> {
            synchronized (reminders) {
                reminders.clear();
                reminders.addAll(firestoreReminders);
            }
        }, e -> {
            //e.printStackTrace();
            Log.e("MyMedsReminders", "Error loading reminders from Firestore: " + e.getMessage());
            initializeSampleData(); // Optionally, fallback to sample data if loading fails.
        });
    }

    public boolean updateReminder(int id, String title, long timeInMillis, String notes) {
        Optional<Reminder> optionalReminder = getReminderById(id);
        if (optionalReminder.isPresent()) {
            Reminder reminder = optionalReminder.get();
            reminder.setTitle(title);
            reminder.setTimeInMillis(timeInMillis);
            reminder.setNotes(notes);
            updateReminderInFirestore(reminder);
            return true;
        }
        return false; // Reminder not found
    }

    public boolean removeReminder(int id) {
        Optional<Reminder> reminderOpt = getReminderById(id);
        if (reminderOpt.isPresent()) {
            boolean removed = reminders.removeIf(reminder -> reminder.getId() == id);
            if (removed) {
                removeReminderFromFirestore(id);
            }
            return removed;
        }
        return false;
    }

    public List<Reminder> getAllReminders() {
        //return Collections.unmodifiableList(reminders);
        return new ArrayList<>(reminders);
    }

    public List<Reminder> getRemindersForDate(LocalDate date) {
        return reminders.stream().filter(reminder -> convertToLocalDate(reminder.getTimeInMillis()).equals(date)).collect(Collectors.toList());
    }

    private Optional<Reminder> getReminderById(int id) {
        return reminders.stream().filter(reminder -> reminder.getId() == id).findFirst();
    }

    private LocalDate convertToLocalDate(long timeInMillis) {
        return Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int generateReminderId() {
        long timestamp = System.currentTimeMillis() / 10000; // Shorter Unix timestamp
        int randomPart = (int) (Math.random() * 9000) + 1001; // 4-digit random number
        //return Integer.parseInt(String.format(Locale.US, "%d%d", timestamp, randomPart));
        return (int) (timestamp + randomPart);
    }

    private void saveReminderToFirestore(Reminder reminder) {
        firestoreReminderManager.saveReminder(reminder, unused -> {
            Log.i("MyMedsReminders", "Reminder saved to Firestore successfully.");
        }, e -> {
            //e.printStackTrace();
            Log.e("MyMedsReminders", "Error saving reminder to Firestore: " + e.getMessage());
        });
    }

    private void updateReminderInFirestore(Reminder reminder) {
        saveReminderToFirestore(reminder); // Overwrite existing document using set()
    }

    private void removeReminderFromFirestore(int id) {
        firestoreReminderManager.deleteReminder(id, unused -> {
            Log.i("MyMedsReminders", "Reminder deleted from Firestore successfully.");
        }, e -> {
            //e.printStackTrace();
            Log.e("MyMedsReminders", "Error deleting reminder from Firestore: " + e.getMessage());
        });
    }

    // New method to refresh the local list from Firestore
    public void loadRemindersFromFirestore(OnSuccessListener<List<Reminder>> onSuccessListener,
                                           OnFailureListener onFailureListener) {
        firestoreReminderManager.loadReminders(onSuccessListener, onFailureListener);
    }

//    public void loadRemindersFromFirestore(OnSuccessListener<List<Reminder>> onSuccessListener, OnFailureListener onFailureListener) {
//        firestoreReminderManager.loadReminders(firestoreReminders -> {
//            // Update the local list with the data from Firestore
//            reminders.clear();
//            reminders.addAll(firestoreReminders);
//            // Return a copy of the updated list
//            onSuccessListener.onSuccess(new ArrayList<>(reminders));
//        }, onFailureListener);
//    }
}
