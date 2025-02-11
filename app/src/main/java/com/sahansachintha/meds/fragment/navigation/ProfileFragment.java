package com.sahansachintha.meds.fragment.navigation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.utils.AlarmScheduler;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button alarmTestButton = view.findViewById(R.id.alarm_test);
        alarmTestButton.setOnClickListener(v -> {
            scheduleAlarm(getContext(), 1); // Replace 1 with the actual reminder ID
        });

        return view;
    }

    public static void scheduleAlarm(Context context, int reminderId) {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MINUTE, 1); // Schedule 1 minutes from now
        calendar.add(Calendar.SECOND, 15); // Schedule 15 seconds from now

        AlarmScheduler.scheduleReminder(context, reminderId, calendar);
    }

}