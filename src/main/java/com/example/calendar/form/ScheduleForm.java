package com.example.calendar.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleForm {
    @NotBlank(message = "予定名を入力してください。")
    @Size(max = 80, message = "予定名は80文字以内で入力してください。")
    private String title;
    @NotNull(message = "日付を入力してください。")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate eventDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) private LocalTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) private LocalTime endTime;
    @Size(max = 500, message = "メモは500文字以内で入力してください。") private String memo;
    private String color = "sage";

    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public LocalDate getEventDate() { return eventDate; } public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalTime getStartTime() { return startTime; } public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; } public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getMemo() { return memo; } public void setMemo(String memo) { this.memo = memo; }
    public String getColor() { return color; } public void setColor(String color) { this.color = color; }
}
