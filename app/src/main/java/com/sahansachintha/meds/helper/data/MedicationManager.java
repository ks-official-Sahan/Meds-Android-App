package com.sahansachintha.meds.helper.data;

import android.util.Log;

import com.sahansachintha.meds.helper.firestore.FirestoreMedicationManager;
import com.sahansachintha.meds.model.Medication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicationManager {

    public static final String TAG = "MyMedsMedications";
    private static volatile MedicationManager instance;
    private final List<Medication> medications;
    private final FirestoreMedicationManager firestoreMedicationManager;

    private MedicationManager() {
        this.medications = new ArrayList<>();
        this.firestoreMedicationManager = new FirestoreMedicationManager();
        //initializeSampleData();
        updateMedicationListFromFirebase();
    }

    public static MedicationManager getInstance() {
        if (instance == null) {
            synchronized (MedicationManager.class) {
                if (instance == null) {
                    instance = new MedicationManager();
                }
            }
        }
        return instance;
    }

    private void initializeSampleData() {
//        addMedication(1, "Paracetamol", "500mg", "Once per day", "After Eating", "https://picsum.photos/700/800?random=15", "Active");
//        addMedication(2, "Ibuprofen", "200mg", "Twice per day", "Before Eating", "https://picsum.photos/700/800?random=16", "Active");
//        addMedication(3, "Amoxicillin", "500mg", "Once per day", "After Eating", "https://picsum.photos/700/800?random=17", "Active");
//        addMedication(4, "Aspirin", "300mg", "Twice per day", "Before Eating", "https://picsum.photos/700/800?random=18", "Inactive");
    }

    public List<Medication> getAllMedications() {
        return new ArrayList<>(medications);
    }

    public List<Medication> getActiveMedications() {
        return medications.stream()
                .filter(medication -> "Active".equalsIgnoreCase(medication.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean addMedication(int id, String name, String dosage, String frequency, String instructions, String status) {
        if (getMedicationById(id).isPresent()) {
            return false; // Prevent duplicate IDs
        }
        Medication medication = new Medication(id, name, dosage, frequency, instructions, status);
        boolean added = medications.add(medication);
        if (added) {
            saveMedicationToFirestore(medication);
        }
        return added;
    }

    public boolean addMedication(int id, String name, String dosage, String frequency, String instructions, String image, String status) {
        if (getMedicationById(id).isPresent()) {
            return false; // Prevent duplicate IDs
        }
        Medication medication = new Medication(id, name, dosage, frequency, instructions, image, status);
        boolean added = medications.add(medication);
        if (added) {
            saveMedicationToFirestore(medication);
        }
        return added;
    }

    public boolean updateMedication(int id, String name, String dosage, String frequency, String instructions) {
        Optional<Medication> optionalMedication = getMedicationById(id);
        if (optionalMedication.isPresent()) {
            Medication medication = optionalMedication.get();
            medication.setName(name);
            medication.setDosage(dosage);
            medication.setFrequency(frequency);
            medication.setInstructions(instructions);
            updateMedicationInFirestore(medication);
            return true;
        }
        return false; // Medication with the given ID not found
    }

    public boolean removeMedication(int id) {
        boolean removed = medications.removeIf(medication -> medication.getId() == id);
        if (removed) {
            removeMedicationFromFirestore(id);
        }
        return removed;
    }

    private Optional<Medication> getMedicationById(int id) {
        return medications.stream().filter(medication -> medication.getId() == id).findFirst();
    }

    public static int generateMedicationId() {
        long timestamp = System.currentTimeMillis() / 10000; // Shorter Unix timestamp
        int randomPart = (int) (Math.random() * 9000) + 1000; // 4-digit random number
        return (int) (timestamp + randomPart);
    }

    // --- Firestore Integration Methods ---

    private void saveMedicationToFirestore(Medication medication) {
        firestoreMedicationManager.saveMedication(medication,
                unused -> Log.i(TAG, "Medication saved to Firestore successfully."),
                e -> Log.e(TAG, "Error saving medication to Firestore: " + e.getMessage())
        );
    }

    private void updateMedicationInFirestore(Medication medication) {
        // For simplicity, re-save the medication (overwriting the existing document)
        saveMedicationToFirestore(medication);
    }

    private void removeMedicationFromFirestore(int id) {
        firestoreMedicationManager.deleteMedication(id,
                unused -> Log.i(TAG, "Medication deleted from Firestore successfully."),
                e -> Log.e(TAG, "Error deleting medication from Firestore: " + e.getMessage())
        );
    }

    public void updateMedicationListFromFirebase() {
        firestoreMedicationManager.loadMedications(firestoreMedications -> {
            synchronized (medications) {
                medications.clear();
                medications.addAll(firestoreMedications);
            }
        }, e -> {
            //e.printStackTrace();
            Log.e(TAG, "Error loading medications from Firestore: " + e.getMessage());
            initializeSampleData(); // Optionally, reinitialize sample data on failure
        });
    }
}