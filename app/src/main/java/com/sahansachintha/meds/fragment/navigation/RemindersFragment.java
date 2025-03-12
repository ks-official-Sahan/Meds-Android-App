package com.sahansachintha.meds.fragment.navigation;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.AddReminderActivity;
import com.sahansachintha.meds.activity.store.StoreActivity;
import com.sahansachintha.meds.adapters.DateAdapter;
import com.sahansachintha.meds.adapters.ReminderAdapter;
import com.sahansachintha.meds.helper.AlarmHelper;
import com.sahansachintha.meds.helper.GenericSwipeCallback;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.DateManager;
import com.sahansachintha.meds.helper.data.ReminderManager;
import com.sahansachintha.meds.model.Reminder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemindersFragment extends Fragment {

    private LocalDate selectedDate = LocalDate.now();

    private RecyclerView dateRecycler, reminderRecycler;
    private List<LocalDate> calendarData;
    private List<Reminder> reminderData;
    private DateAdapter dateAdapter;
    private ReminderAdapter reminderAdapter;
    private LinearLayoutManager dateLayoutManager;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reminders, container, false);

        toggleView(R.id.add_reminders_container, false);
        toggleView(R.id.reminders_container, true);

        initDateRecycler();
        initReminderRecycler();
        setUpListeners();

        attachSwipeAction();

        /* return view */
        return view;
    }

    private void setUpListeners() {
        setClickListener(R.id.fab_today_reminders, () -> {
            selectDate(LocalDate.now());
            updateReminders(getReminderData());
        });

        setClickListener(R.id.reminders_view_all, this::viewAll);
        setClickListener(R.id.reminders_refresh, this::viewAll);
        setClickListener(R.id.fab_add_reminders, () -> NavigationHelper.getInstance().openIntent(requireContext(), AddReminderActivity.class));
        view.findViewById(R.id.fab_add_reminders).setOnLongClickListener(v -> {
            toggleView(R.id.add_reminders_container, true);
            toggleView(R.id.reminders_container, false);
            return false;
        });

        setClickListener(R.id.fab_shop_reminders, () -> {
            try {
                NavigationHelper.getInstance().openIntent(requireContext(), StoreActivity.class);
            } catch (IllegalStateException e) {
                Log.e("MyMedsReminders", "Error opening StoreActivity: " + e.getMessage());
            }
        });

        int[] alarmButtons = {R.id.alarm_test_15s, R.id.alarm_test_30s, R.id.alarm_test_1m, R.id.alarm_test_5m, R.id.alarm_test_10m};
        int[] alarmTimes = {15, 30, 60, 300, 600};

        for (int i = 0; i < alarmButtons.length; i++) {
            int delay = alarmTimes[i];
            int finalI = i;
            setClickListener(alarmButtons[i], () -> scheduleAlarm(finalI + 1, Calendar.SECOND, delay));
        }
    }

    private void initDateRecycler() {
        dateRecycler = view.findViewById(R.id.reminders_date_recycler);
        dateLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecycler.setLayoutManager(dateLayoutManager);

        calendarData = DateManager.getInstance().generateCalendarData();
        dateAdapter = new DateAdapter(getContext(), calendarData, selectedDate, this::onDateSelected);
        dateRecycler.setAdapter(dateAdapter);

        dateRecycler.post(() -> scrollToPositionSmooth(calendarData.indexOf(selectedDate)));
    }

    private void initReminderRecycler() {
        reminderRecycler = view.findViewById(R.id.reminders_recycler);
        LinearLayoutManager reminderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        reminderRecycler.setLayoutManager(reminderLayoutManager);

        // Apply layout animation for a smooth appearance.
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        reminderRecycler.setLayoutAnimation(controller);
        reminderRecycler.setItemAnimator(new DefaultItemAnimator());

        reminderData = new ArrayList<>(ReminderManager.getInstance().getAllReminders());
        toggleView(R.id.reminders_empty_view, reminderData.isEmpty());

        reminderAdapter = new ReminderAdapter(reminderData, getContext());
        reminderRecycler.setAdapter(reminderAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateReminders(List<Reminder> reminders) {
        reminderData.clear();
        reminderData.addAll(reminders);
        reminderAdapter.notifyDataSetChanged();

        toggleView(R.id.reminders_empty_view, reminderData.isEmpty());
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        dateAdapter.setSelectedDate(selectedDate);
        scrollToPositionSmooth(calendarData.indexOf(selectedDate));
    }

    private void scrollToPositionSmooth(int position) {
        if (position >= 0 && position < calendarData.size()) {
            dateLayoutManager.scrollToPositionWithOffset(position, (dateRecycler.getWidth() / 2) - 120);
        }
    }

    private void viewAll() {
        selectDate(LocalDate.now());
        updateReminders(ReminderManager.getInstance().getAllReminders());
    }

    private List<Reminder> getReminderData() {
        return ReminderManager.getInstance().getRemindersForDate(selectedDate);
    }

    private void scheduleAlarm(int reminderId, int field, int amount) {
        AlarmHelper.getInstance().scheduleAlarm(getContext(), reminderId, field, amount);

        toggleView(R.id.add_reminders_container, false);
        toggleView(R.id.reminders_container, true);
    }

    private void toggleView(int viewId, boolean visible) {
        view.findViewById(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setClickListener(int viewId, Runnable action) {
        view.findViewById(viewId).setOnClickListener(v -> action.run());
    }

    private void onDateSelected(LocalDate date) {
        selectDate(date);
        updateReminders(getReminderData());
    }

    private void attachSwipeAction() {
        // Right swipe: mark as done; left swipe: delete
        GenericSwipeCallback.SwipeActionListener swipeListener = new GenericSwipeCallback.SwipeActionListener() {
            @Override
            public void onSwipeRight(int position) {
                Reminder reminder = reminderData.get(position);
                Toast.makeText(getContext(), "Marked as done: " + reminder.getTitle(), Toast.LENGTH_SHORT).show();
                // In a real app, update the status and persist the change
                reminderAdapter.notifyItemChanged(position);
            }

            @Override
            public void onSwipeLeft(int position) {
                Reminder reminder = reminderData.get(position);
                Toast.makeText(getContext(), "Deleted: " + reminder.getTitle(), Toast.LENGTH_SHORT).show();
                reminderAdapter.removeItem(position);
            }
        };

        GenericSwipeCallback swipeCallback = new GenericSwipeCallback(
                getContext(),
                swipeListener,
                ContextCompat.getDrawable(getContext(), R.drawable.ic_delete), // left icon for delete
                Color.parseColor("#F44336"),                                // left background (red)
                "Delete",                                                   // left text
                ContextCompat.getDrawable(getContext(), R.drawable.ic_done),   // right icon for done
                Color.parseColor("#4CAF50"),                                // right background (green)
                "Done"                                                      // right text
        ) {};

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(reminderRecycler);
    }
}