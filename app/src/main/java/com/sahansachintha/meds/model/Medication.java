package com.sahansachintha.meds.model;

import java.io.Serializable;
import java.util.Calendar;

public class Medication implements Serializable {

    private int id;
    private String name;
    private String dosage;
    private String notes;

    private String time;

    private String image; // Store Image URL instead of binary data

//    private long timeInMillis;
//    private Calendar calendar;

    public Medication(int id, String name, String dosage, String time, String notes) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.notes = notes;
    }

    public Medication(int id, String name, String dosage, String notes, String time, String image) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.notes = notes;
        this.time = time;
        this.image = image;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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