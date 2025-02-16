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
import com.sahansachintha.meds.activity.StoreActivity;
import com.sahansachintha.meds.adapters.DateAdapter;
import com.sahansachintha.meds.adapters.MedicationAdapter;
import com.sahansachintha.meds.adapters.ReminderAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.MedicationManager;
import com.sahansachintha.meds.helper.data.ReminderManager;
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

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

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

        try {
            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            view.findViewById(R.id.fab_shop).setVisibility(View.GONE);
            Log.e("MyMedsHome", "Cannot Find Resource with ID R.id.fab in HomeFragment");
        }

        //MaterialCardView btnShop = view.findViewById(R.id.btn_shop);
        FloatingActionButton btnShop = view.findViewById(R.id.fab_shop);
        btnShop.setOnClickListener(v -> {
            if (getContext() != null) {
                try {
                    NavigationHelper.getInstance().openIntent(requireContext(), StoreActivity.class);
                } catch (IllegalStateException e) {
                    Log.e("MyMedsHome", "Error opening StoreActivity: " + e.getMessage());
                }
            }
        });

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
        List<Reminder> reminderList;

        reminderList = ReminderManager.getInstance().getSampleData();

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
        List<Medication> medicationList;

        medicationList = MedicationManager.getInstance().getSampleData();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.e("MyMedsHome", "Cannot Find Resource with ID R.id.fab in HomeFragment");
        }
    }
}