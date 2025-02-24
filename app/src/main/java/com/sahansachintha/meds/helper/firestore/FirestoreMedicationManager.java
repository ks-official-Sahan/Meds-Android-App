package com.sahansachintha.meds.helper.firestore;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sahansachintha.meds.model.Medication;
import com.sahansachintha.meds.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreMedicationManager {

    private static final String COLLECTION_MEDICATIONS = "medications";
    private final FirebaseFirestore db;
    private String token;

    public FirestoreMedicationManager() {
        db = FirebaseFirestore.getInstance();
        ApiService.getToken(token1 -> token = token1);
    }

    // Save a medication to Firestore
    public void saveMedication(Medication medication,
                               OnSuccessListener<Void> onSuccessListener,
                               OnFailureListener onFailureListener) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", medication.getId());
        data.put("name", medication.getName());
        data.put("dosage", medication.getDosage());
        data.put("frequency", medication.getFrequency());
        data.put("instructions", medication.getInstructions());
        data.put("image", medication.getImage());
        data.put("status", medication.getStatus());
        data.put("user_token", token);
        // Add any additional fields if needed

        db.collection(COLLECTION_MEDICATIONS)
                .document(String.valueOf(medication.getId()))
                .set(data)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Load all medications from Firestore
    public void loadMedications(OnSuccessListener<List<Medication>> onSuccessListener,
                                OnFailureListener onFailureListener) {
        db.collection(COLLECTION_MEDICATIONS)
                .whereEqualTo("user_token", token)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Medication> medications = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        try {
                            int id = doc.getLong("id").intValue();
                            String name = doc.getString("name");
                            String dosage = doc.getString("dosage");
                            String frequency = doc.getString("frequency");
                            String instructions = doc.getString("instructions");
                            String image = doc.getString("image");
                            String status = doc.getString("status");

                            Medication medication = new Medication(id, name, dosage, instructions, frequency, image, status);
                            medications.add(medication);
                        } catch (Exception e) {
                            Log.e("MyMedsFirestoreMedicationManager", "Error converting medication data. " + e.getMessage());
                        }
                    }
                    onSuccessListener.onSuccess(medications);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void deleteMedication(int id,
                               OnSuccessListener<Void> onSuccessListener,
                               OnFailureListener onFailureListener) {
        db.collection(COLLECTION_MEDICATIONS)
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}