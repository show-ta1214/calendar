package com.example.calendar.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JapaneseHolidayService {
    public Map<LocalDate, String> holidays(int year) {
        Map<LocalDate, String> holidays = baseHolidays(year);
        addCitizensHolidays(year, holidays);
        addSubstituteHolidays(year, holidays);
        return holidays;
    }

    private Map<LocalDate, String> baseHolidays(int year) {
        Map<LocalDate, String> holidays = new LinkedHashMap<>();
        add(holidays, year, 1, 1, "元日");
        add(holidays, nthMonday(year, Month.JANUARY, 2), "成人の日");
        add(holidays, year, 2, 11, "建国記念の日");
        if (year >= 2020) add(holidays, year, 2, 23, "天皇誕生日");
        add(holidays, LocalDate.of(year, 3, vernalEquinoxDay(year)), "春分の日");
        add(holidays, year, 4, 29, year >= 2007 ? "昭和の日" : "みどりの日");
        add(holidays, year, 5, 3, "憲法記念日");
        if (year >= 2007) add(holidays, year, 5, 4, "みどりの日");
        add(holidays, year, 5, 5, "こどもの日");

        if (year == 2020) add(holidays, year, 7, 23, "海の日");
        else if (year == 2021) add(holidays, year, 7, 22, "海の日");
        else add(holidays, nthMonday(year, Month.JULY, 3), "海の日");

        if (year == 2020) add(holidays, year, 8, 10, "山の日");
        else if (year == 2021) add(holidays, year, 8, 8, "山の日");
        else if (year >= 2016) add(holidays, year, 8, 11, "山の日");

        add(holidays, nthMonday(year, Month.SEPTEMBER, 3), "敬老の日");
        add(holidays, LocalDate.of(year, 9, autumnEquinoxDay(year)), "秋分の日");

        if (year == 2020) add(holidays, year, 7, 24, "スポーツの日");
        else if (year == 2021) add(holidays, year, 7, 23, "スポーツの日");
        else add(holidays, nthMonday(year, Month.OCTOBER, 2), year >= 2020 ? "スポーツの日" : "体育の日");

        add(holidays, year, 11, 3, "文化の日");
        add(holidays, year, 11, 23, "勤労感謝の日");
        if (year <= 2018) add(holidays, year, 12, 23, "天皇誕生日");
        if (year == 2019) {
            add(holidays, year, 4, 30, "国民の休日");
            add(holidays, year, 5, 1, "天皇の即位の日");
            add(holidays, year, 5, 2, "国民の休日");
            add(holidays, year, 10, 22, "即位礼正殿の儀");
        }
        return holidays;
    }

    private void addCitizensHolidays(int year, Map<LocalDate, String> holidays) {
        LocalDate date = LocalDate.of(year, 1, 2);
        LocalDate end = LocalDate.of(year, 12, 30);
        while (!date.isAfter(end)) {
            if (!holidays.containsKey(date) && holidays.containsKey(date.minusDays(1)) && holidays.containsKey(date.plusDays(1))) {
                holidays.put(date, "国民の休日");
            }
            date = date.plusDays(1);
        }
    }

    private void addSubstituteHolidays(int year, Map<LocalDate, String> holidays) {
        for (Map.Entry<LocalDate, String> holiday : Map.copyOf(holidays).entrySet()) {
            if (holiday.getKey().getDayOfWeek() != DayOfWeek.SUNDAY) continue;
            LocalDate substitute = holiday.getKey().plusDays(1);
            while (holidays.containsKey(substitute)) substitute = substitute.plusDays(1);
            if (substitute.getYear() == year) holidays.put(substitute, "振替休日");
        }
    }

    private LocalDate nthMonday(int year, Month month, int nth) {
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.dayOfWeekInMonth(nth, DayOfWeek.MONDAY));
    }

    private int vernalEquinoxDay(int year) {
        return (int) Math.floor(20.8431 + 0.242194 * (year - 1980) - Math.floor((year - 1980) / 4.0));
    }

    private int autumnEquinoxDay(int year) {
        return (int) Math.floor(23.2488 + 0.242194 * (year - 1980) - Math.floor((year - 1980) / 4.0));
    }

    private void add(Map<LocalDate, String> holidays, int year, int month, int day, String name) {
        add(holidays, LocalDate.of(year, month, day), name);
    }

    private void add(Map<LocalDate, String> holidays, LocalDate date, String name) {
        holidays.put(date, name);
    }
}
