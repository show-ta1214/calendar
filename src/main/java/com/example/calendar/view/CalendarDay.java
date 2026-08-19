package com.example.calendar.view;

import com.example.calendar.entity.Schedule;
import java.time.LocalDate;
import java.util.List;

public record CalendarDay(LocalDate date, boolean currentMonth, boolean today, String holidayName, List<Schedule> schedules) {
    public boolean holiday() { return holidayName != null; }
}
