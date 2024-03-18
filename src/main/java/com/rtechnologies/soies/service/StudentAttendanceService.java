package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.StudentAttendance;
import com.rtechnologies.soies.model.association.StudentAttendanceFinal;
import com.rtechnologies.soies.model.dto.MarkAttendanceResponse;
import com.rtechnologies.soies.repository.StudentAttendanceFinalRepository;
import com.rtechnologies.soies.repository.StudentAttendanceRepository;
import com.rtechnologies.soies.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
        if(student.isEmpty()){
            response.setStatus("No student found with roll number: " + studentRollNum);
            return response;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Mark attendance as present when student logs in
        StudentAttendance attendance = new StudentAttendance();
        attendance.setStudentRollNum(studentRollNum);
        attendance.setStatus("Present");
        attendance.setDate(LocalDate.now());
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
        if(studentAttendance.isEmpty()) {
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
                .messageStatus("Success")
                .build();

        return response;
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at 12 AM every day
    public void autoAttendanceMarking(String studentRollNum) {
        List<Student> students = studentRepository.findAll();

        for(Student student : students) {
            // Calculate total time spent and mark attendance as absent if less than 15 minutes
            List<StudentAttendance> todayAttendance = attendanceRepository.findByStudentRollNumAndDate(student.getRollNumber(),
                    LocalDate.now());
            int totalMinutesSpent = calculateTotalMinutesSpent(todayAttendance);
            String attendanceStatus = totalMinutesSpent >= 15 ? "Present" : "Absent";

            StudentAttendanceFinal studentAttendanceFinal = new StudentAttendanceFinal();
            studentAttendanceFinal.setStudentRollNum(studentRollNum);
            studentAttendanceFinal.setDate(LocalDate.now());
            studentAttendanceFinal.setTotalTimeSpentInMinutes(totalMinutesSpent);
            studentAttendanceFinal.setStatus(attendanceStatus);
            studentAttendanceFinal.setLastLoginTime(todayAttendance.get(todayAttendance.size()-1).getLastLoginTime());

            studentAttendanceFinalRepository.save(studentAttendanceFinal);
        }

    }

    private int calculateTotalMinutesSpent(List<StudentAttendance> attendanceList) {
        // Calculate total minutes spent in the session
        int totalMinutes = 0;
        for (StudentAttendance attendance : attendanceList) {
            // Calculate difference between login and logout time
            LocalTime loginTime = attendance.getLastLoginTime();
            LocalTime logoutTime = LocalTime.now(); // Assuming logout time is current time
            long minutesSpent = ChronoUnit.MINUTES.between(loginTime, logoutTime);
            totalMinutes += minutesSpent;
        }
        return totalMinutes;
    }
}
