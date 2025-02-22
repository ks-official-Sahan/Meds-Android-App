package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.model.Medication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MedicationManager {

    private static volatile MedicationManager instance;
    private final List<Medication> medications;

    private MedicationManager() {
        this.medications = new ArrayList<>();
        initializeSampleData();
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
        addMedication(1, "Paracetamol", "500mg", "Once per day", "After Eating");
        addMedication(2, "Ibuprofen", "200mg", "Twice per day", "Before Eating");
        addMedication(3, "Amoxicillin", "500mg", "Once per day", "After Eating");
        addMedication(4, "Aspirin", "300mg", "Twice per day", "Before Eating");
    }

    public List<Medication> getAllMedications() {
        //return Collections.unmodifiableList(medications);
        return  new ArrayList<>(medications);
    }

    public boolean addMedication(int id, String name, String dosage, String frequency, String instructions) {
        if (getMedicationById(id).isPresent()) {
            return false; // ID already exists, prevent duplicate
        }
        return medications.add(new Medication(id, name, dosage, frequency, instructions));
    }

    public boolean updateMedication(int id, String name, String dosage, String frequency, String instructions) {
        Optional<Medication> optionalMedication = getMedicationById(id);
        if (optionalMedication.isPresent()) {
            Medication medication = optionalMedication.get();
            medication.setName(name);
            medication.setDosage(dosage);
            medication.setFrequency(frequency);
            medication.setInstructions(instructions);
            return true;
        }
        return false; // Medication with ID not found
    }

    public boolean removeMedication(int id) {
        return medications.removeIf(medication -> medication.getId() == id);
    }

    private Optional<Medication> getMedicationById(int id) {
        return medications.stream().filter(medication -> medication.getId() == id).findFirst();
    }

    public static int generateMedicationId() {
        long timestamp = System.currentTimeMillis() / 10000; // Shorter Unix timestamp
        int randomPart = (int) (Math.random() * 9000) + 1000; // 4-digit random number
        return Integer.parseInt(String.format(Locale.US, "%d%d", timestamp, randomPart));
    }
}
