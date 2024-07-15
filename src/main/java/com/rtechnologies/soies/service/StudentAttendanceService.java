package com.rtechnologies.soies.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.StudentAttendance;
import com.rtechnologies.soies.model.association.StudentAttendanceFinal;
import com.rtechnologies.soies.model.dto.MarkAttendanceResponse;
import com.rtechnologies.soies.repository.StudentAttendanceFinalRepository;
import com.rtechnologies.soies.repository.StudentAttendanceRepository;
import com.rtechnologies.soies.repository.StudentRepository;
import com.rtechnologies.soies.utilities.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@EnableScheduling
@Service
public class StudentAttendanceService {

    @Autowired
    private StudentAttendanceRepository attendanceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentAttendanceFinalRepository studentAttendanceFinalRepository;

    public MarkAttendanceResponse markAttendanceOnLogin(String studentRollNum) {
        Optional<Student> student = studentRepository.findByRollNumber(studentRollNum);
        MarkAttendanceResponse response = new MarkAttendanceResponse();
        if (student.isEmpty()) {
            response.setStatus("No student found with roll number: " + studentRollNum);
            return response;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Mark attendance as present when student logs in
        StudentAttendance attendance = new StudentAttendance();
        attendance.setStudentRollNum(studentRollNum);
        attendance.setStatus("Present");
        attendance.setDate(LocalDate.now());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!NOW____________________" + LocalTime.now());
        attendance.setLastLoginTime(LocalTime.now());
        attendance = attendanceRepository.save(attendance);

        String formattedDate = attendance.getDate().format(formatter);
        response = MarkAttendanceResponse.builder()
                .id(attendance.getId())
                .studentRollNum(attendance.getStudentRollNum())
                .lastLoginTime(attendance.getLastLoginTime())
                .status(attendance.getStatus())
                .date(formattedDate)
                .lastLoginTime(attendance.getLastLoginTime())
                .messageStatus("Success")
                .build();

        return response;
    }

    public MarkAttendanceResponse markAttendanceOnLogout(Long sessionId) {
        Optional<StudentAttendance> studentAttendance = attendanceRepository.findById(sessionId);
        MarkAttendanceResponse response = new MarkAttendanceResponse();
        if (studentAttendance.isEmpty()) {
            response.setStatus("No session found with ID: " + sessionId);
            return response;
        }

        long differenceInMinutes = ChronoUnit.MINUTES.between(LocalTime.now(), studentAttendance.get().getLastLoginTime());

        // If the difference is negative, it means the given time is in the future
        // So, we adjust the difference by adding 24 hours (1440 minutes)
        if (differenceInMinutes < 0) {
            differenceInMinutes += 24 * 60; // 24 hours * 60 minutes
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Mark attendance as present when student logs in
        StudentAttendance attendance = new StudentAttendance();
        attendance.setId(sessionId);
        attendance.setStudentRollNum(studentAttendance.get().getStudentRollNum());
        attendance.setStatus("Present");
        attendance.setDate(LocalDate.now());
        attendance.setLastLoginTime(LocalTime.now());
        attendance.setTotalTimeSpentInMinutes((int) differenceInMinutes);
        attendance = attendanceRepository.save(attendance);

        String formattedDate = attendance.getDate().format(formatter);
        response = MarkAttendanceResponse.builder()
                .id(attendance.getId())
                .studentRollNum(attendance.getStudentRollNum())
                .lastLoginTime(attendance.getLastLoginTime())
                .status(attendance.getStatus())
                .date(formattedDate)
                .lastLoginTime(attendance.getLastLoginTime())
                .totalTimeSpentInMinutes(attendance.getTotalTimeSpentInMinutes())
                .messageStatus("Success")
                .build();

        return response;
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at 12 AM every day
    public void autoAttendanceMarking() throws JsonProcessingException {
        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            // Calculate total time spent and mark attendance as absent if less than 15 minutes
            StudentAttendance todayAttendance = attendanceRepository.findFirstByStudentRollNumAndDateOrderByLastLoginTimeDesc(student.getRollNumber(),
                    LocalDate.now());

            String attendanceStatus = null;
            int totalMinutesSpent = 0;

            if (todayAttendance != null) {
                totalMinutesSpent = Math.abs(calculateTotalMinutesSpent(todayAttendance));
                attendanceStatus = totalMinutesSpent >= 15 ? "Present" : "Absent";
            } else {
                attendanceStatus = "Absent";
            }

            StudentAttendanceFinal studentAttendanceFinal = new StudentAttendanceFinal();
            studentAttendanceFinal.setStudentRollNum(student.getRollNumber());
            studentAttendanceFinal.setDate(LocalDate.now());
            studentAttendanceFinal.setTotalTimeSpentInMinutes(totalMinutesSpent);
            studentAttendanceFinal.setStatus(attendanceStatus);
            if (todayAttendance != null) {
                studentAttendanceFinal.setLastLoginTime(todayAttendance.getLastLoginTime());
            }

            studentAttendanceFinalRepository.save(studentAttendanceFinal);
        }

    }


    private int calculateTotalMinutesSpent(StudentAttendance attendance) {
        // Calculate total minutes spent in the session
        int totalMinutes = 0;
        LocalTime loginTime = attendance.getLastLoginTime();
        LocalTime logoutTime = LocalTime.now(); // Assuming logout time is current time
        long minutesSpent = ChronoUnit.MINUTES.between(loginTime, logoutTime);
        totalMinutes += minutesSpent;
        return totalMinutes;
    }
}
