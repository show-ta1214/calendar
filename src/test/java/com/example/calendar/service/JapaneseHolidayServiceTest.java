package com.example.calendar.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class JapaneseHolidayServiceTest {
    private final JapaneseHolidayService service = new JapaneseHolidayService();

    @Test void returnsFixedMovingAndEquinoxHolidays() {
        Map<LocalDate, String> holidays = service.holidays(2026);
        assertThat(holidays.get(LocalDate.of(2026, 1, 1))).isEqualTo("元日");
        assertThat(holidays.get(LocalDate.of(2026, 1, 12))).isEqualTo("成人の日");
        assertThat(holidays.get(LocalDate.of(2026, 3, 20))).isEqualTo("春分の日");
        assertThat(holidays.get(LocalDate.of(2026, 8, 11))).isEqualTo("山の日");
    }

    @Test void returnsSubstituteAndCitizensHolidays() {
        assertThat(service.holidays(2024).get(LocalDate.of(2024, 2, 12))).isEqualTo("振替休日");
        assertThat(service.holidays(2026).get(LocalDate.of(2026, 9, 22))).isEqualTo("国民の休日");
    }
}
