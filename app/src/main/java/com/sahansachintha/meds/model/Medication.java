package com.sahansachintha.meds.model;

import java.io.Serializable;

public class Medication implements Serializable {

    private int id;
    private String name;
    private String dosage;
    private String instructions;

    private String frequency;

    private String image; // Store Image URL instead of binary data

    private String status = "Active";

//    private long timeInMillis;
//    private Calendar calendar;

    public Medication(int id, String name, String dosage, String frequency, String instructions) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.instructions = instructions;
    }

    public Medication(int id, String name, String dosage, String instructions, String frequency, String image) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.instructions = instructions;
        this.frequency = frequency;
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    //    public long getTimeInMillis() {
//        return timeInMillis;
//    }
//
//    public void setTimeInMillis(long timeInMillis) {
//        this.timeInMillis = timeInMillis;
//    }
//
//    public void setCalendar(Calendar calendar) {
//        this.timeInMillis = calendar.getTimeInMillis();
//        this.calendar = calendar;
//    }
//
//    public Calendar getCalendar() {
//        if (this.calendar != null) {
//            return this.calendar;
//        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(timeInMillis);
//        return calendar;
//    }

}