package com.example.calendar.service;

import com.example.calendar.entity.Schedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleDao {
    Schedule save(Schedule schedule);
    Optional<Schedule> findById(Long id);
    List<Schedule> findBetween(LocalDate from, LocalDate to);
    List<Schedule> findUpcoming(LocalDate from, int limit);
    void deleteById(Long id);
}
