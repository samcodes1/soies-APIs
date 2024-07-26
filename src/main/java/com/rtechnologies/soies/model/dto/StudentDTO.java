package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long studentId;
    private String rollNumber;
    private String password;
    private String studentName;
    private String gender;
    private String campusName;
    private String grade;
    private String sectionName;
    private String dateOfBirth;
    private String guardianName;
    private String guardianPhoneNumber;
    private String guardianEmail;
    private String address;
    private String city;
    private StudentAttendanceDTO studentAttendance;
    // Getters and Setters
}
