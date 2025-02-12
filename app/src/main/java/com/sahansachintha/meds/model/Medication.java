package com.sahansachintha.meds.model;

import java.io.Serializable;
import java.util.Calendar;

public class Medication implements Serializable {

    private int id;
    private String name;
    private String dosage;
    private String notes;

    private String time;
//    private long timeInMillis;
//    private Calendar calendar;

    public Medication(int id, String name, String dosage, String time, String notes) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.notes = notes;
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