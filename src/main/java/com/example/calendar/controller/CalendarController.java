package com.example.calendar.controller;

import com.example.calendar.entity.Schedule;
import com.example.calendar.form.ScheduleForm;
import com.example.calendar.service.ScheduleDao;
import com.example.calendar.service.JapaneseHolidayService;
import com.example.calendar.view.CalendarDay;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class CalendarController {
    private final ScheduleDao scheduleDao;
    private final JapaneseHolidayService holidayService;
    public CalendarController(ScheduleDao scheduleDao, JapaneseHolidayService holidayService) {
        this.scheduleDao = scheduleDao;
        this.holidayService = holidayService;
    }

    @GetMapping("/")
    public String calendar(@RequestParam(required = false) String month, Model model) {
        YearMonth shownMonth = parseMonth(month);
        LocalDate first = shownMonth.atDay(1);
        LocalDate gridStart = first.minusDays(first.getDayOfWeek().getValue() % 7);
        LocalDate gridEnd = gridStart.plusDays(41);
        Map<LocalDate, List<Schedule>> schedules = new HashMap<>();
        for (Schedule schedule : scheduleDao.findBetween(gridStart, gridEnd)) {
            LocalDate rangeStart = schedule.getEventDate().isBefore(gridStart) ? gridStart : schedule.getEventDate();
            LocalDate rangeEnd = schedule.getEndDate().isAfter(gridEnd) ? gridEnd : schedule.getEndDate();
            for (LocalDate date = rangeStart; !date.isAfter(rangeEnd); date = date.plusDays(1)) schedules.computeIfAbsent(date, ignored -> new ArrayList<>()).add(schedule);
        }
        Map<LocalDate, String> holidays = new HashMap<>();
        for (int year = gridStart.getYear(); year <= gridEnd.getYear(); year++) holidays.putAll(holidayService.holidays(year));
        List<CalendarDay> days = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            LocalDate date = gridStart.plusDays(i);
            days.add(new CalendarDay(date, YearMonth.from(date).equals(shownMonth), date.equals(LocalDate.now()), holidays.get(date), schedules.getOrDefault(date, List.of())));
        }
        model.addAttribute("shownMonth", shownMonth);
        model.addAttribute("days", days);
        model.addAttribute("previousMonth", shownMonth.minusMonths(1));
        model.addAttribute("nextMonth", shownMonth.plusMonths(1));
        model.addAttribute("upcoming", scheduleDao.findUpcoming(LocalDate.now(), 6));
        return "calendar";
    }

    @GetMapping("/schedules/new")
    public String newSchedule(@RequestParam(required = false) String date, Model model) {
        ScheduleForm form = new ScheduleForm();
        form.setEventDate(parseDate(date));
        form.setEndDate(form.getEventDate());
        model.addAttribute("scheduleForm", form);
        model.addAttribute("editMode", false);
        return "schedule-form";
    }

    @PostMapping("/schedules")
    public String create(@Valid @ModelAttribute ScheduleForm scheduleForm, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        validateSchedule(scheduleForm, result);
        if (result.hasErrors()) { model.addAttribute("editMode", false); return "schedule-form"; }
        scheduleDao.save(toSchedule(scheduleForm));
        redirectAttributes.addFlashAttribute("successMessage", "予定を登録しました。");
        return "redirect:/?month=" + YearMonth.from(scheduleForm.getEventDate());
    }

    @GetMapping("/schedules/{id}/edit")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Schedule> found = scheduleDao.findById(id);
        if (found.isEmpty()) return missing(redirectAttributes);
        Schedule schedule = found.get();
        ScheduleForm form = new ScheduleForm();
        form.setTitle(schedule.getTitle()); form.setEventDate(schedule.getEventDate()); form.setEndDate(schedule.getEndDate()); form.setStartTime(schedule.getStartTime());
        form.setEndTime(schedule.getEndTime()); form.setMemo(schedule.getMemo()); form.setColor(schedule.getColor());
        model.addAttribute("scheduleForm", form); model.addAttribute("editMode", true); model.addAttribute("scheduleId", id);
        return "schedule-form";
    }

    @PostMapping("/schedules/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ScheduleForm scheduleForm, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        validateSchedule(scheduleForm, result);
        if (result.hasErrors()) { model.addAttribute("editMode", true); model.addAttribute("scheduleId", id); return "schedule-form"; }
        Schedule schedule = scheduleDao.findById(id).orElse(null);
        if (schedule == null) return missing(redirectAttributes);
        schedule.update(scheduleForm.getTitle(), scheduleForm.getEventDate(), scheduleForm.getEndDate(), scheduleForm.getStartTime(), scheduleForm.getEndTime(), scheduleForm.getMemo(), safeColor(scheduleForm.getColor()));
        scheduleDao.save(schedule);
        redirectAttributes.addFlashAttribute("successMessage", "予定を更新しました。");
        return "redirect:/?month=" + YearMonth.from(scheduleForm.getEventDate());
    }

    @PostMapping("/schedules/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Schedule schedule = scheduleDao.findById(id).orElse(null);
        if (schedule == null) return missing(redirectAttributes);
        YearMonth month = YearMonth.from(schedule.getEventDate()); scheduleDao.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "予定を削除しました。");
        return "redirect:/?month=" + month;
    }

    private Schedule toSchedule(ScheduleForm f) { return new Schedule(f.getTitle(), f.getEventDate(), f.getEndDate(), f.getStartTime(), f.getEndTime(), f.getMemo(), safeColor(f.getColor())); }
    private String safeColor(String color) { return Set.of("sage", "coral", "ochre", "sky", "lavender", "blue", "purple").contains(color) ? color : "sage"; }
    private void validateTimes(ScheduleForm f, BindingResult result) { if (f.getStartTime() != null && f.getEndTime() != null && !f.getEndTime().isAfter(f.getStartTime())) result.rejectValue("endTime", "time.order", "終了時刻は開始時刻より後にしてください。"); }
    private void validateSchedule(ScheduleForm f, BindingResult result) { if (f.getEventDate() != null && f.getEndDate() != null && f.getEndDate().isBefore(f.getEventDate())) result.rejectValue("endDate", "date.order", "終了日は開始日以降にしてください。"); validateTimes(f, result); }
    private String missing(RedirectAttributes attributes) { attributes.addFlashAttribute("errorMessage", "対象の予定が見つかりません。"); return "redirect:/"; }
    private YearMonth parseMonth(String value) { try { return value == null ? YearMonth.now() : YearMonth.parse(value); } catch (DateTimeParseException e) { return YearMonth.now(); } }
    private LocalDate parseDate(String value) { try { return value == null ? LocalDate.now() : LocalDate.parse(value); } catch (DateTimeParseException e) { return LocalDate.now(); } }
}
