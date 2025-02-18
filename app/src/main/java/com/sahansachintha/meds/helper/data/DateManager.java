package com.sahansachintha.meds.helper.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateManager {

    private static DateManager dateManager;

    private DateManager() {
    }

    public static DateManager getInstance() {
        if (dateManager == null) {
            dateManager = new DateManager();
        }
        return dateManager;
    }

    public List<LocalDate> generateCalendarData() {
        return generateCalendarData(6, 6);
    }

    public List<LocalDate> generateCalendarData(long monthsToSubtract, long monthsToAdd) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusMonths(monthsToSubtract); // months before
        LocalDate endDate = LocalDate.now().plusMonths(monthsToAdd); // months after

        while (!startDate.isAfter(endDate)) {
            days.add(startDate);
            startDate = startDate.plusDays(1);
        }

        return days;
    }
}
