package com.rtechnologies.soies.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class StudentAttendanceDTO {
    private Long id;
    private String studentRollNum;
    private String status;
    private LocalDate date;
    private LocalTime lastLoginTime;
    private int totalTimeSpentInMinutes;
    // Getters and Setters
}