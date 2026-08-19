package com.example.calendar.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80) private String title;
    @Column(nullable = false) private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    @Column(length = 500) private String memo;
    @Column(nullable = false, length = 20) private String color;

    protected Schedule() {}
    public Schedule(String title, LocalDate eventDate, LocalTime startTime, LocalTime endTime, String memo, String color) {
        update(title, eventDate, startTime, endTime, memo, color);
    }
    public void update(String title, LocalDate eventDate, LocalTime startTime, LocalTime endTime, String memo, String color) {
        this.title = title; this.eventDate = eventDate; this.startTime = startTime; this.endTime = endTime;
        this.memo = memo; this.color = color;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getMemo() { return memo; }
    public String getColor() { return color; }
}
