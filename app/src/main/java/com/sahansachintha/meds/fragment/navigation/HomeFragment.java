package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.DateAdapter;
import com.sahansachintha.meds.adapters.MedicationAdapter;
import com.sahansachintha.meds.adapters.ReminderAdapter;
import com.sahansachintha.meds.model.Medication;
import com.sahansachintha.meds.model.Reminder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private LocalDate selectedDate = LocalDate.now();

    /* Date */
    private RecyclerView dateRecyler;
    private List<LocalDate> calendarData;
    private DateAdapter dateAdapter;
    private LinearLayoutManager dateLayoutManager;

    /* Reminder */
    private RecyclerView reminderRecyler;
    private List<Reminder> reminderData;
    private ReminderAdapter reminderAdapter;
    private LinearLayoutManager reminderLayoutManager;

    /* Medication */
    private RecyclerView medicationRecycler;
    private List<Medication> medicationData;
    private MedicationAdapter medicationAdapter;
    private LinearLayoutManager medicationLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dateRecyler = view.findViewById(R.id.date_recycler);
        initDateRecyler();

        reminderRecyler = view.findViewById(R.id.reminder_recycler);
        initReminderRecyler();

        medicationRecycler = view.findViewById(R.id.medication_recycler);
        initMedicationRecyler();

        //MaterialCardView btnToday = view.findViewById(R.id.btn_today);
        FloatingActionButton btnToday = view.findViewById(R.id.fab_today);
        btnToday.setOnClickListener(v -> {
            selectedDate = LocalDate.now();
            dateAdapter.setSelectedDate(selectedDate);
            scrollToPositionSmooth(calendarData.indexOf(selectedDate));
        });

        //MaterialCardView btnShop = view.findViewById(R.id.btn_shop);
//        FloatingActionButton btnShop = view.findViewById(R.id.fab_shop_home);
//        btnShop.setOnClickListener(v -> {
//            if (getContext() != null) {
//                try {
//                    NavigationHelper.getInstance().openIntent(this.requireContext(), StoreActivity.class);
//                } catch (IllegalStateException e) {
//                    Log.e("MyMedsHome", "Error opening StoreActivity: " + e.getMessage());
//                }
//            }
//        });

        return view;
    }

    private void initDateRecyler() {
        dateLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyler.setLayoutManager(dateLayoutManager);

        calendarData = generateCalendarData();

        //DateAdapter dateAdapter = new DateAdapter(getContext(), calendarData, selectedDate);
        dateAdapter = new DateAdapter(getContext(), calendarData, selectedDate, date -> {
            selectedDate = date;
            Log.d("MyMedsHome", "Selected Date: " + date);
            scrollToPositionSmooth(calendarData.indexOf(date));
        });
        dateRecyler.setAdapter(dateAdapter);

        dateRecyler.post(() -> scrollToPositionSmooth(calendarData.indexOf(selectedDate)));
    }

    private void initReminderRecyler() {
        reminderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        reminderRecyler.setLayoutManager(reminderLayoutManager);

        reminderData = getReminderData();

        //ReminderAdapter reminderAdapter = new ReminderAdapter(reminderData, getContext());
        reminderAdapter = new ReminderAdapter(reminderData, getContext());
        reminderRecyler.setAdapter(reminderAdapter);
    }

    private List<Reminder> getReminderData() {
        List<Reminder> reminderList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        calendar.set(2025, Calendar.FEBRUARY, 12, 8, 30); // 12th Feb, 8:30 AM
        reminderList.add(new Reminder(1, "Reminder 1", calendar.getTimeInMillis(), "Finish Reminder Development"));

        calendar.set(2025, Calendar.FEBRUARY, 12, 10, 30); // 12th Feb, 8:30 AM
        reminderList.add(new Reminder(2, "Reminder 2", calendar.getTimeInMillis(), "Finish Medication Development"));

        calendar.set(2025, Calendar.FEBRUARY, 13, 8, 30); // 13th Feb, 8:30 AM
        reminderList.add(new Reminder(3, "Reminder 3", calendar.getTimeInMillis(), "Finish Store Home Development"));

        calendar.set(2025, Calendar.FEBRUARY, 14, 8, 30); // 14th Feb, 8:30 AM
        reminderList.add(new Reminder(4, "Reminder 4", calendar.getTimeInMillis(), "Finish Store Product View Development"));

        return reminderList;
    }

    private void initMedicationRecyler() {
        medicationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        medicationRecycler.setLayoutManager(medicationLayoutManager);

        medicationData = getMedicationData();

        //MedicationAdapter medicationAdapter = new MedicationAdapter(medicationData, getContext());
        medicationAdapter = new MedicationAdapter(medicationData, getContext());
        medicationRecycler.setAdapter(medicationAdapter);
    }

    private List<Medication> getMedicationData() {
        List<Medication> medicationList = new ArrayList<>();

        medicationList.add(new Medication(1, "Paracetamol", "500mg", "Once per day", "After Eating"));
        medicationList.add(new Medication(2, "Ibuprofen", "200mg", "Twice per day", "Before Eating"));
        medicationList.add(new Medication(3, "Amoxicillin", "500mg", "Once per day", "After Eating"));
        medicationList.add(new Medication(4, "Aspirin", "300mg", "Twice per day", "Before Eating"));

        return medicationList;
    }

    private List<LocalDate> generateCalendarData() {
        List<LocalDate> days = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusMonths(6); // 6 months before
        LocalDate endDate = LocalDate.now().plusMonths(6); // 6 months after

        while (!startDate.isAfter(endDate)) {
            days.add(startDate);
            startDate = startDate.plusDays(1);
        }

        return days;
    }

    private void scrollToPositionSmooth(int position) {
        //dateRecyler.smoothScrollToPosition(position);
        if (position >= 0 && position < calendarData.size()) {
            //layoutManager.scrollToPositionWithOffset(position, 0); // start
            dateLayoutManager.scrollToPositionWithOffset(position, (dateRecyler.getWidth() / 2) - 120); // center
        }
    }

}