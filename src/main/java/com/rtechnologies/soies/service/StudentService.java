package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.dto.StudentListResponse;
import com.rtechnologies.soies.model.dto.StudentResponse;
import com.rtechnologies.soies.repository.StudentRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public StudentResponse createStudent(Student student) {
        Utility.printDebugLogs("Student creation request: " + student.toString());
        StudentResponse studentResponse;

        try {
            if (student == null) {
                Utility.printDebugLogs("Student creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for existing student
            Optional<Student> existingStudent = studentRepository.findByRollNumber(student.getRollNumber());
            if (existingStudent.isPresent()) {
                throw new IllegalArgumentException("Student with Roll Number " + student.getRollNumber() + " already exists");
            }

            String hashedPassword = new BCryptPasswordEncoder().encode(student.getPassword());
            student.setPassword(hashedPassword);
            Student createdStudent = studentRepository.save(student);
            Utility.printDebugLogs("Student created successfully: " + createdStudent);

            studentResponse = StudentResponse.builder()
                    .studentId(createdStudent.getStudentId())
                    .rollNumber(createdStudent.getRollNumber())
                    .studentName(createdStudent.getStudentName())
                    .gender(createdStudent.getGender())
                    .campusName(createdStudent.getCampusName())
                    .className(createdStudent.getGrade())
                    .sectionName(createdStudent.getSectionName())
                    .dateOfBirth(createdStudent.getDateOfBirth())
                    .guardianName(createdStudent.getGuardianName())
                    .guardianPhoneNumber(createdStudent.getGuardianPhoneNumber())
                    .guardianEmail(createdStudent.getGuardianEmail())
                    .address(createdStudent.getAddress())
                    .city(createdStudent.getCity())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student response: " + studentResponse);
            return studentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public StudentResponse updateStudent(Student student) {
        Utility.printDebugLogs("Student update request: " + student.toString());
        StudentResponse studentResponse;

        try {
            if (student == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for existing student
            Optional<Student> existingStudent = studentRepository.findByRollNumber(student.getRollNumber());
            if (existingStudent.isEmpty()) {
                throw new IllegalArgumentException("No Student found with Roll Number: " + student.getRollNumber());
            }

            Student updatedStudent = studentRepository.save(student);
            Utility.printDebugLogs("Student updated successfully: " + updatedStudent);

            studentResponse = StudentResponse.builder()
                    .studentId(updatedStudent.getStudentId())
                    .rollNumber(updatedStudent.getRollNumber())
                    .studentName(updatedStudent.getStudentName())
                    .gender(updatedStudent.getGender())
                    .campusName(updatedStudent.getCampusName())
                    .className(updatedStudent.getGrade())
                    .sectionName(updatedStudent.getSectionName())
                    .dateOfBirth(updatedStudent.getDateOfBirth())
                    .guardianName(updatedStudent.getGuardianName())
                    .guardianPhoneNumber(updatedStudent.getGuardianPhoneNumber())
                    .guardianEmail(updatedStudent.getGuardianEmail())
                    .address(updatedStudent.getAddress())
                    .city(updatedStudent.getCity())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student response: " + studentResponse);
            return studentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public StudentResponse deleteStudent(String rollNumber) {
        Utility.printDebugLogs("Student deletion request: " + rollNumber);
        StudentResponse studentResponse;

        try {
            Optional<Student> existingStudent = studentRepository.findByRollNumber(rollNumber);

            if (existingStudent.isEmpty()) {
                throw new IllegalArgumentException("No student found with Roll Number: " + rollNumber);
            }

            studentRepository.deleteByRollNumber(rollNumber);
            Utility.printDebugLogs("Student deleted successfully: " + existingStudent.get());

            studentResponse = StudentResponse.builder()
                    .studentId(existingStudent.get().getStudentId())
                    .rollNumber(existingStudent.get().getRollNumber())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student response: " + studentResponse);
            return studentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public StudentResponse getStudentByRollNumber(String rollNumber) {
        Utility.printDebugLogs("Get student by Roll Number: " + rollNumber);
        StudentResponse studentResponse;

        try {
            Optional<Student> optionalStudent = studentRepository.findByRollNumber(rollNumber);

            if (optionalStudent.isEmpty()) {
                throw new IllegalArgumentException("No student found with Roll Number: " + rollNumber);
            }

            Student student = optionalStudent.get();

            studentResponse = StudentResponse.builder()
                    .studentId(student.getStudentId())
                    .rollNumber(student.getRollNumber())
                    .studentName(student.getStudentName())
                    .gender(student.getGender())
                    .campusName(student.getCampusName())
                    .className(student.getGrade())
                    .sectionName(student.getSectionName())
                    .dateOfBirth(student.getDateOfBirth())
                    .guardianName(student.getGuardianName())
                    .guardianPhoneNumber(student.getGuardianPhoneNumber())
                    .guardianEmail(student.getGuardianEmail())
                    .address(student.getAddress())
                    .city(student.getCity())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student response: " + studentResponse);
            return studentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public StudentListResponse getAllStudentsByCampusName(String campusName, int page, int size) {
        Utility.printDebugLogs("Get all students by campus name with pagination");
        StudentListResponse studentListResponse;

        try {
            Page<Student> studentPage = studentRepository.findAllByCampusName(campusName, PageRequest.of(page, size));

            if (studentPage.isEmpty()) {
                throw new IllegalArgumentException("No students found for campus: " + campusName);
            }

            studentListResponse = StudentListResponse.builder()
                    .studentList(studentPage.getContent())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student list response: " + studentListResponse);
            return studentListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public StudentListResponse getAllStudents(int page, int size) {
        Utility.printDebugLogs("Get all students with pagination");
        StudentListResponse studentListResponse;

        try {
            Page<Student> studentPage = studentRepository.findAll(PageRequest.of(page, size));

            if (studentPage.isEmpty()) {
                throw new IllegalArgumentException("No students found");
            }

            studentListResponse = StudentListResponse.builder()
                    .studentList(studentPage.getContent())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student list response: " + studentListResponse);
            return studentListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }
}
