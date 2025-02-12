package com.sahansachintha.meds.fragment.navigation;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.Calendar;

public class RemindersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        Button alarmTest15sButton = view.findViewById(R.id.alarm_test_15s);
        alarmTest15sButton.setOnClickListener(v -> {
            scheduleAlarm(getContext(), 1); // Replace 1 with the actual reminder ID
        });

        Button alarmTest30sButton = view.findViewById(R.id.alarm_test_30s);
        alarmTest30sButton.setOnClickListener(v -> {
            scheduleAlarm(getContext(), 2, Calendar.SECOND, 30); // Replace 2 with the actual reminder ID
        });

        Button alarmTest1mButton = view.findViewById(R.id.alarm_test_1m);
        alarmTest1mButton.setOnClickListener(v -> {
            scheduleAlarm(getContext(), 3, Calendar.MINUTE, 1); // Replace 3 with the actual reminder ID
        });

        Button alarmTest5mButton = view.findViewById(R.id.alarm_test_5m);
        alarmTest5mButton.setOnClickListener(v -> {
            scheduleAlarm(getContext(), 4, Calendar.MINUTE, 5); // Replace 4 with the actual reminder ID
        });

        Button alarmTest10mButton = view.findViewById(R.id.alarm_test_10m);
        alarmTest10mButton.setOnClickListener(v -> {
            scheduleAlarm(getContext(), 5, Calendar.MINUTE, 10); // Replace 5 with the actual reminder ID
        });

        return view;
    }

    public void scheduleAlarm(Context context, int reminderId) {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MINUTE, 1); // Schedule 1 minutes from now
        calendar.add(Calendar.SECOND, 15); // Schedule 15 seconds from now

        AlarmScheduler.scheduleReminder(context, reminderId, calendar);
    }

    public void scheduleAlarm(Context context, int reminderId, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        AlarmScheduler.scheduleReminder(context, reminderId, calendar);
    }

}