package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.DateAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private LocalDate selectedDate = LocalDate.now();
    private RecyclerView dateRecyler;
    private List<LocalDate> calendarData;
    private LinearLayoutManager layoutManager;
    private DateAdapter dateAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        dateRecyler = view.findViewById(R.id.date_recycler);
        initDateRecyler();

        MaterialCardView btnToday = view.findViewById(R.id.btn_today);
        btnToday.setOnClickListener(v -> {
            selectedDate = LocalDate.now();
            dateAdapter.setSelectedDate(selectedDate);
            scrollToPositionSmooth(calendarData.indexOf(selectedDate));
        });

        return view;
    }

    private void initDateRecyler() {
        dateRecyler.setLayoutManager(layoutManager);

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
            layoutManager.scrollToPositionWithOffset(position, (dateRecyler.getWidth() / 2) - 120); // center
        }
    }

}