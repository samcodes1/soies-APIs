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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExcelParser excelParser;

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

    @Transactional
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
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> studentPage = studentRepository.findAllByCampusName(campusName, pageable);

            if (studentPage.isEmpty()) {
                // throw new IllegalArgumentException("No students found for campus: " + campusName);
                List<Student> emptyList = new ArrayList<>();
                studentListResponse = StudentListResponse.builder()
                    .studentList(emptyList)
                    .messageStatus("Success")
                    .build();
                return studentListResponse;
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

    public StudentListResponse getAllStudentsByGradeCourseSection(String campusName, String course, String grade, String section, int page, int size) {
        Utility.printDebugLogs("Get all students with pagination");
        StudentListResponse studentListResponse;

        try {
            Pageable pageable = PageRequest.of(page, size);
            List<Student> studentPage = null;
            if(course==null && grade ==null && section==null){
                studentPage = studentRepository.findbycampus(campusName, pageable);
            }
            else if(course!=null && grade ==null && section==null){
                studentPage = studentRepository.findbycampusAndCourse(campusName, course, pageable);
            }
            else if(course!=null && grade !=null && section==null){
                studentPage = studentRepository.findbycampusAndCourseAndGrade(campusName, course, grade, pageable);
            }
            else if(course!=null && grade !=null && section!=null){
                studentPage = studentRepository.findbycampusAndCourseAndGradeAndSection(campusName, course, grade, section, pageable);
            }
            else if(course==null && grade !=null && section!=null){
                studentPage = studentRepository.findbycampusAndGradeAndSection(campusName, grade, section, pageable);
            }
            else if(course==null && grade ==null && section!=null){
                studentPage = studentRepository.findbycampusAndSection(campusName, section, pageable);
            }
            else if(course==null && grade !=null && section==null){
                studentPage = studentRepository.findbycampusAndGrade(campusName, grade, pageable);
            }
            else{
                studentPage = studentRepository.findByGradeAndSectionNameAndStudentCourses(campusName, grade, section, course, pageable);
            }

            // if (studentPage.isEmpty()) {
            //     throw new IllegalArgumentException("No students found");
            // }

            studentListResponse = StudentListResponse.builder()
                    .studentList(studentPage)
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
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> studentPage = studentRepository.findAll(pageable);

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

     public StudentListResponse saveStudentsFromFile(MultipartFile file) throws IOException {
        List<Student> students = null;
        if(Utility.isCSV(file)){
            // System.out.println();
            students = excelParser.csvParserStudent(file);
            List<Student> duplicates = new ArrayList<>();
            List<Student> newStudents = new ArrayList<>();
            for (Student student : students) {
                Optional<Student> existingStudent = studentRepository.findByRollNumber(student.getRollNumber());
                if (existingStudent.isPresent()) {
                    duplicates.add(student);
                } else {
                    newStudents.add(student);
                }
            }
    
            // Save new students
            studentRepository.saveAll(newStudents);
            // studentRepository.saveAll(students);
            StudentListResponse studentListResponse = StudentListResponse.builder()
                        .studentList(duplicates)
                        .messageStatus("Success")
                        .build();
            return studentListResponse;
        }else if(Utility.isExcel(file)){
            students = excelParser.parseStudentExcelFile(file.getInputStream());
            studentRepository.saveAll(students);
            StudentListResponse studentListResponse = StudentListResponse.builder()
                        .studentList(null)
                        .messageStatus("Success")
                        .build();
            return studentListResponse;
        }
        throw new IllegalArgumentException("Wrong file received!");
    }
}
