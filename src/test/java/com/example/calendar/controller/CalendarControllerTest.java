package com.example.calendar.controller;

import com.example.calendar.entity.Schedule;
import com.example.calendar.service.ScheduleDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class CalendarControllerTest {
    @Autowired MockMvc mockMvc; @Autowired ScheduleDao scheduleDao;
    @Test void newScheduleFormDisplaysAllFiveColors() throws Exception { mockMvc.perform(get("/schedules/new")).andExpect(status().isOk()).andExpect(content().string(containsString("class=\"color-options\""))).andExpect(content().string(containsString("value=\"sage\""))).andExpect(content().string(containsString("value=\"coral\""))).andExpect(content().string(containsString("value=\"ochre\""))).andExpect(content().string(containsString("value=\"blue\""))).andExpect(content().string(containsString("value=\"purple\""))); }
    @Test void monthPageDisplaysRequestedMonthAndHoliday() throws Exception { mockMvc.perform(get("/").param("month","2026-08")).andExpect(status().isOk()).andExpect(view().name("calendar")).andExpect(content().string(containsString("2026年8月"))).andExpect(content().string(containsString("山の日"))).andExpect(content().string(containsString("holiday"))); }
    @Test void scheduleCanBeCreatedEditedAndDeleted() throws Exception {
        mockMvc.perform(post("/schedules").param("title","打ち合わせ").param("eventDate","2026-08-18").param("endDate","2026-08-20").param("startTime","10:00").param("endTime","11:00").param("color","sage"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/?month=2026-08"));
        Schedule schedule = scheduleDao.findBetween(LocalDate.of(2026,8,18),LocalDate.of(2026,8,18)).stream().filter(s->s.getTitle().equals("打ち合わせ")).findFirst().orElseThrow();
        assertThat(schedule.getEndDate()).isEqualTo(LocalDate.of(2026,8,20));
        mockMvc.perform(get("/").param("month","2026-08")).andExpect(content().string(org.hamcrest.Matchers.stringContainsInOrder("18", "打ち合わせ", "19", "打ち合わせ", "20", "打ち合わせ")));
        mockMvc.perform(post("/schedules/{id}",schedule.getId()).param("title","定例会議").param("eventDate","2026-08-19").param("endDate","2026-08-19").param("startTime","13:00").param("endTime","14:00").param("color","coral"))
                .andExpect(status().is3xxRedirection()); assertThat(scheduleDao.findById(schedule.getId()).orElseThrow().getTitle()).isEqualTo("定例会議");
        mockMvc.perform(post("/schedules/{id}/delete",schedule.getId())).andExpect(status().is3xxRedirection()); assertThat(scheduleDao.findById(schedule.getId())).isEmpty();
    }
    @Test void invalidScheduleReturnsForm() throws Exception { mockMvc.perform(post("/schedules").param("title","").param("eventDate","2026-08-18").param("endDate","2026-08-17").param("startTime","11:00").param("endTime","10:00")) .andExpect(status().isOk()).andExpect(view().name("schedule-form")).andExpect(content().string(containsString("予定名を入力してください。"))).andExpect(content().string(containsString("終了日は開始日以降にしてください。"))).andExpect(content().string(containsString("終了時刻は開始時刻より後にしてください。"))); }
}
