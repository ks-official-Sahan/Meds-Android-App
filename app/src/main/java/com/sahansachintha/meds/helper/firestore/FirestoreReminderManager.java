package com.sahansachintha.meds.helper.firestore;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sahansachintha.meds.model.Reminder;
import com.sahansachintha.meds.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreReminderManager {

    private static final String COLLECTION_REMINDERS = "reminders";
    private final FirebaseFirestore db;
    private String token;

    public FirestoreReminderManager() {
        db = FirebaseFirestore.getInstance();
        ApiService.getToken(token1 -> token = token1);
    }

    // Save a reminder to Firestore
    public void saveReminder(Reminder reminder,
                             OnSuccessListener<Void> onSuccessListener,
                             OnFailureListener onFailureListener) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", reminder.getId());
        data.put("title", reminder.getTitle());
        data.put("timeInMillis", reminder.getTimeInMillis());
        data.put("notes", reminder.getNotes());
        data.put("user_token", token);
        // You can add additional fields as needed

        db.collection(COLLECTION_REMINDERS)
                .document(String.valueOf(reminder.getId()))
                .set(data)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Load all reminders from Firestore
    public void loadReminders(OnSuccessListener<List<Reminder>> onSuccessListener,
                              OnFailureListener onFailureListener) {
        db.collection(COLLECTION_REMINDERS)
                .whereEqualTo("user_token", token)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Reminder> reminders = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        // Firestore stores numbers as Long so we convert as needed
                        try {
                            int id = Objects.requireNonNull(doc.getLong("id")).intValue();
                            String title = doc.getString("title");
                            long timeInMillis = doc.getLong("timeInMillis");
                            String notes = doc.getString("notes");

                            Reminder reminder = new Reminder(id, title, timeInMillis, notes);
                            reminders.add(reminder);
                        } catch (NullPointerException e) {
                            Log.e("MyMedsFirestoreReminderManager", "Error converting reminder data. " + e.getMessage());
                        }
                    }
                    onSuccessListener.onSuccess(reminders);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void deleteReminder(int id,
                               OnSuccessListener<Void> onSuccessListener,
                               OnFailureListener onFailureListener) {
        db.collection(COLLECTION_REMINDERS)
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
