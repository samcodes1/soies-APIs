package com.rtechnologies.soies.model.association;

import com.rtechnologies.soies.model.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentRollNum; // Renamed from stuId
    private String status;
    private LocalDate date; // Changed to LocalDate
    private LocalTime lastLoginTime; // Changed to LocalTime
    private int totalTimeSpentInMinutes; // Changed and made numerical for easier calculation

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "studentId")
    private Student student;
}
