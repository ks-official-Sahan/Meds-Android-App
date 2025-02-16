package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.model.Medication;

import java.util.ArrayList;

public class MedicationManager {

    private static MedicationManager categoryManager;

    private MedicationManager() {
    }

    public static MedicationManager getInstance() {
        if (categoryManager == null) {
            categoryManager = new MedicationManager();
        }
        return categoryManager;
    }

    public ArrayList<Medication> getSampleData() {
        ArrayList<Medication> medicationList = new ArrayList<>();

        medicationList.add(new Medication(1, "Paracetamol", "500mg", "Once per day", "After Eating"));
        medicationList.add(new Medication(2, "Ibuprofen", "200mg", "Twice per day", "Before Eating"));
        medicationList.add(new Medication(3, "Amoxicillin", "500mg", "Once per day", "After Eating"));
        medicationList.add(new Medication(4, "Aspirin", "300mg", "Twice per day", "Before Eating"));


        return medicationList;
    }

}
