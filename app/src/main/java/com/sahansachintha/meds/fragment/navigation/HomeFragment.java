package com.sahansachintha.meds.fragment.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.HomeActivity;
import com.sahansachintha.meds.activity.store.StoreActivity;
import com.sahansachintha.meds.adapters.DateAdapter;
import com.sahansachintha.meds.adapters.MedicationAdapter;
import com.sahansachintha.meds.adapters.ReminderAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.DateManager;
import com.sahansachintha.meds.helper.data.MedicationManager;
import com.sahansachintha.meds.helper.data.ReminderManager;
import com.sahansachintha.meds.model.Medication;
import com.sahansachintha.meds.model.Reminder;

import java.time.LocalDate;
import java.util.List;

public class HomeFragment extends Fragment {
    private LocalDate selectedDate = LocalDate.now();
    private RecyclerView dateRecycler, reminderRecycler, medicationRecycler;
    private DateAdapter dateAdapter;
    private ReminderAdapter reminderAdapter;
    private MedicationAdapter medicationAdapter;
    private List<LocalDate> calendarData;
    private List<Reminder> reminderData;
    private List<Medication> medicationData;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initDateRecycler();
        initReminderRecycler();
        initMedicationRecycler();

//        try {
//            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
//        } catch (NullPointerException e) {
//            view.findViewById(R.id.fab_shop).setVisibility(View.GONE);
//            Log.e("MyMedsHome", "Cannot Find Resource with ID R.id.fab in HomeFragment");
//        }
        view.findViewById(R.id.fab_today).setOnClickListener(v -> selectDate(LocalDate.now()));

        view.findViewById(R.id.reminder_view_all_home).setOnClickListener(v -> viewAllReminders());
        view.findViewById(R.id.reminder_refresh_home).setOnClickListener(v -> viewAllReminders());
        view.findViewById(R.id.medication_view_all_home).setOnClickListener(v -> viewAllMedications());
        view.findViewById(R.id.medication_refresh_home).setOnClickListener(v -> viewAllMedications());

        view.findViewById(R.id.fab_shop).setOnClickListener(v -> openStore());

        return view;
    }

    private void openStore() {
        try {
            NavigationHelper.getInstance().openIntent(requireContext(), StoreActivity.class);
        } catch (IllegalStateException e) {
            Log.e("MyMedsHome", "Error opening StoreActivity: " + e.getMessage());
        }
    }

    private void viewAllReminders() {
        //navigateTo(HomeActivity.class, R.id.menu_item_reminders);
        if (HomeActivity.getNavigationView() != null) {
            HomeActivity.getBottomNavigationView().setSelectedItemId(R.id.menu_item_reminders);
        }
    }

    private void viewAllMedications() {
        //navigateTo(HomeActivity.class, R.id.menu_item_medications);
        if (HomeActivity.getNavigationView() != null) {
            HomeActivity.getBottomNavigationView().setSelectedItemId(R.id.menu_item_medications);
        }
    }

    private void navigateTo(Class<?> target, int menuItem) {
        NavigationHelper.getInstance().openIntent(requireContext(), target, menuItem);
    }

    private void initDateRecycler() {
        dateRecycler = view.findViewById(R.id.date_recycler);
        dateRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        calendarData = DateManager.getInstance().generateCalendarData();
        dateAdapter = new DateAdapter(getContext(), calendarData, selectedDate, this::onDateSelected);
        dateRecycler.setAdapter(dateAdapter);

        dateRecycler.post(() -> scrollToPosition(calendarData.indexOf(selectedDate)));
    }

    private void onDateSelected(LocalDate date) {
        selectDate(date);
        updateReminders(ReminderManager.getInstance().getRemindersForDate(selectedDate));
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        dateAdapter.setSelectedDate(selectedDate);
        scrollToPosition(calendarData.indexOf(selectedDate));
    }

    private void scrollToPosition(int position) {
        //dateRecyler.smoothScrollToPosition(position);
        if (position >= 0 && position < calendarData.size()) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) dateRecycler.getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(position, (dateRecycler.getWidth() / 2) - 120);
            //layoutManager.scrollToPositionWithOffset(position, 0); // start
        }
    }

    private void initReminderRecycler() {
        reminderRecycler = view.findViewById(R.id.reminder_recycler);
        reminderRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        reminderData = ReminderManager.getInstance().getRemindersForDate(selectedDate);
        reminderAdapter = new ReminderAdapter(reminderData, getContext());
        reminderRecycler.setAdapter(reminderAdapter);

        toggleView(R.id.reminder_empty_view_home, reminderData.isEmpty());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateReminders(List<Reminder> reminders) {
        reminderData.clear();
        reminderData.addAll(reminders);
        reminderAdapter.notifyDataSetChanged();
        toggleView(R.id.reminder_empty_view_home, reminderData.isEmpty());
    }

    private void initMedicationRecycler() {
        medicationRecycler = view.findViewById(R.id.medication_recycler);
        medicationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //medicationData = MedicationManager.getInstance().getAllMedications();
        medicationData = MedicationManager.getInstance().getActiveMedications();
        medicationAdapter = new MedicationAdapter(medicationData, getContext());
        medicationRecycler.setAdapter(medicationAdapter);

        toggleView(R.id.medication_empty_view_home, medicationData.isEmpty());
    }

    private void toggleView(int viewId, boolean isVisible) {
        view.findViewById(viewId).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        try {
//            getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
//        } catch (NullPointerException e) {
//            Log.e("MyMedsHome", "Cannot Find Resource with ID R.id.fab in HomeFragment");
//        }
//    }
}