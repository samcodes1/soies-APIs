package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.*;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExcelParser excelParser;

    @Autowired
    StudentAttendanceRepository attendanceRepository;

    @Autowired
    StudentCourseRepository studentCourseRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    OgaSubmissionRepository ogaSubmissionRepository;

    @Autowired
    ExamSubmissionRepository examSubmissionRepository;

    @Autowired
    AssignmentSubmissionRepository assignmentSubmissionRepository;

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
                throw new IllegalArgumentException(
                        "Student with Roll Number " + student.getRollNumber() + " already exists");
            }

            String hashedPassword = new BCryptPasswordEncoder().encode(student.getPassword());
            student.setPassword(hashedPassword);
            Student createdStudent = studentRepository.save(student);
            Utility.printDebugLogs("Student created successfully: " + createdStudent);

            // Fetch courses for the student's grade
            String studentGrade = createdStudent.getGrade();
            List<Course> courses = getCoursesForGrade(studentGrade);

            // Create StudentCourse relationships
            List<StudentCourse> studentCourses = new ArrayList<>();
            for (Course course : courses) {
                StudentCourse studentCourse = StudentCourse.builder()
                        .studentId(createdStudent.getStudentId())
                        .courseId(course.getCourseId())
                        .build();
                studentCourses.add(studentCourse);
            }

            // Save student-course relationships
            studentCourseRepository.saveAll(studentCourses);

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

            // Delete associated courses
            List<StudentCourse> studentCourses = studentCourseRepository
                    .findAllByStudentId(existingStudent.get().getStudentId());
            if (!studentCourses.isEmpty()) {
                studentCourseRepository.deleteAll(studentCourses);
            }

            // Delete student
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
                // throw new IllegalArgumentException("No students found for campus: " +
                // campusName);
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

    public StudentListResponseDTO getAllStudentsByGradeCourseSection(String campusName, String course, String grade,
            String section, int page, int size) {
        Utility.printDebugLogs("Get all students with pagination");
        StudentListResponseDTO studentListResponse;

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> studentPage = null;

            // Determine the correct query to use based on the provided parameters
            if (campusName != null && course == null && grade == null && section == null) {
                studentPage = studentRepository.findbycampus(campusName, pageable);
            } else if (campusName != null && course != null && grade == null && section == null) {
                studentPage = studentRepository.findbycampusAndCourse(campusName, course, pageable);
            } else if (campusName != null && course != null && grade != null && section == null) {
                studentPage = studentRepository.findbycampusAndCourseAndGrade(campusName, course, grade, pageable);
            } else if (campusName != null && course != null && grade != null && section != null) {
                studentPage = studentRepository.findbycampusAndCourseAndGradeAndSection(campusName, course, grade,
                        section, pageable);
            } else if (campusName != null && course == null && grade != null && section != null) {
                studentPage = studentRepository.findbycampusAndGradeAndSection(campusName, grade, section, pageable);
            } else if (campusName != null && course == null && grade == null && section != null) {
                studentPage = studentRepository.findbycampusAndSection(campusName, section, pageable);
            } else if (campusName != null && course == null && grade != null && section == null) {
                studentPage = studentRepository.findbycampusAndGrade(campusName, grade, pageable);
            } else if (campusName != null && course != null && grade != null && section != null) {
                studentPage = studentRepository.findByGradeAndSectionNameAndStudentCourses(campusName, grade, section,
                        course, pageable);
            } else {
                throw new IllegalArgumentException("Invalid parameters");
            }

            if (studentPage.isEmpty()) {
                studentListResponse = StudentListResponseDTO.builder()
                        .studentPage(new PageImpl<>(Collections.emptyList(), pageable, 0))
                        .messageStatus("Success")
                        .build();
                Utility.printDebugLogs("Student list response: " + studentListResponse);
                return studentListResponse;
            }

            // Fetch and set the latest StudentAttendance for each student
            List<StudentDTO> studentsWithAttendance = studentPage.getContent().stream()
                    .map(student -> {
                        StudentDTO studentDTO = mapToStudentDTO(student);
                        List<StudentAttendance> attendanceList = attendanceRepository
                                .findLatestByStudentRollNum(student.getRollNumber());
                        if (!attendanceList.isEmpty()) {
                            studentDTO.setStudentAttendance(mapToStudentAttendanceDTO(attendanceList.get(0))); // Set
                                                                                                               // the
                                                                                                               // latest
                                                                                                               // entry
                        } else {
                            StudentAttendanceDTO defaultAttendance = new StudentAttendanceDTO();
                            defaultAttendance.setStatus("Absent");
                            defaultAttendance.setDate(null); // Example of setting current date
                            defaultAttendance.setLastLoginTime(null); // Example of setting default time
                            defaultAttendance.setTotalTimeSpentInMinutes(0);
                            studentDTO.setStudentAttendance(defaultAttendance); // Set default values
                        }
                        return studentDTO;
                    })
                    .collect(Collectors.toList());

            // Create a new Page object with updated students
            Page<StudentDTO> updatedStudentPage = new PageImpl<>(studentsWithAttendance, pageable,
                    studentPage.getTotalElements());

            studentListResponse = StudentListResponseDTO.builder()
                    .studentPage(updatedStudentPage)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Student list response: " + studentListResponse);
            return studentListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return StudentListResponseDTO.builder()
                    .studentPage(new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0))
                    .messageStatus("Success")
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return StudentListResponseDTO.builder()
                    .studentPage(new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0))
                    .messageStatus("Failure")
                    .build();
        }
    }

    private StudentDTO mapToStudentDTO(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId(student.getStudentId());
        studentDTO.setRollNumber(student.getRollNumber());
        studentDTO.setPassword(student.getPassword());
        studentDTO.setStudentName(student.getStudentName());
        studentDTO.setGender(student.getGender());
        studentDTO.setCampusName(student.getCampusName());
        studentDTO.setGrade(student.getGrade());
        studentDTO.setSectionName(student.getSectionName());
        studentDTO.setDateOfBirth(student.getDateOfBirth());
        studentDTO.setGuardianName(student.getGuardianName());
        studentDTO.setGuardianPhoneNumber(student.getGuardianPhoneNumber());
        studentDTO.setGuardianEmail(student.getGuardianEmail());
        studentDTO.setAddress(student.getAddress());
        studentDTO.setCity(student.getCity());
        return studentDTO;
    }

    private StudentAttendanceDTO mapToStudentAttendanceDTO(StudentAttendance studentAttendance) {
        StudentAttendanceDTO studentAttendanceDTO = new StudentAttendanceDTO();
        studentAttendanceDTO.setId(studentAttendance.getId());
        studentAttendanceDTO.setStudentRollNum(studentAttendance.getStudentRollNum());
        studentAttendanceDTO.setStatus(studentAttendance.getStatus());
        studentAttendanceDTO.setDate(studentAttendance.getDate());
        studentAttendanceDTO.setLastLoginTime(studentAttendance.getLastLoginTime());
        studentAttendanceDTO.setTotalTimeSpentInMinutes(studentAttendance.getTotalTimeSpentInMinutes());
        return studentAttendanceDTO;
    }

    public StudentListResponse getAllStudentsWithPagination(int page, int size) {
        Utility.printDebugLogs("Get all students with pagination");
        StudentListResponse studentListResponse;

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> studentPage = studentRepository.findAll(pageable);

            if (studentPage.isEmpty()) {
                throw new IllegalArgumentException("No students found");
            }

            List<Student> studentsWithAttendance = studentPage.getContent().stream()
                    .map(student -> {
                        List<StudentAttendance> attendanceList = attendanceRepository
                                .findLatestByStudentRollNum(student.getRollNumber());
                        if (!attendanceList.isEmpty()) {
                            student.setStudentAttendance(attendanceList.get(0)); // Set the latest entry
                        }
                        return student;
                    })
                    .collect(Collectors.toList());

            studentListResponse = StudentListResponse.builder()
                    .studentList(studentsWithAttendance)
                    .studentPage(studentPage)
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

    @Transactional
    public StudentListResponse saveStudentsFromFile(MultipartFile file) throws IOException {
        List<Student> students = null;
        List<StudentCourse> studentCourses = new ArrayList<>();

        try {
            if (Utility.isCSV(file)) {
                students = excelParser.csvParserStudent(file);
            } else if (Utility.isExcel(file)) {
                students = excelParser.parseStudentExcelFile(file.getInputStream());
            } else {
                throw new IllegalArgumentException("Wrong file received!");
            }

            List<Student> newStudents = new ArrayList<>();
            List<Student> duplicates = new ArrayList<>();

            for (Student student : students) {
                Optional<Student> existingStudent = studentRepository.findByRollNumber(student.getRollNumber());
                if (existingStudent.isPresent()) {
                    duplicates.add(student);
                } else {
                    newStudents.add(student);
                    studentRepository.saveAll(newStudents);
                    // Fetch courses for the student's grade
                    String studentGrade = student.getGrade();
                    List<Course> courses = getCoursesForGrade(studentGrade);

                    // Create StudentCourse relationships
                    for (Course course : courses) {
                        StudentCourse studentCourse = StudentCourse.builder()
                                .studentId(student.getStudentId())
                                .courseId(course.getCourseId())
                                .build();
                        studentCourses.add(studentCourse);
                    }
                }
            }

            studentCourseRepository.saveAll(studentCourses);

            return StudentListResponse.builder()
                    .studentList(duplicates)
                    .messageStatus("Success")
                    .build();
        } catch (Exception e) {
            // Log detailed error
            Utility.printErrorLogs("Error while saving students from file: " + e.getMessage());
            throw e; // Rethrow or handle as needed
        }
    }

    private List<Course> getCoursesForGrade(String grade) {
        // Fetch courses by grade from the repository
        return courseRepository.findCoursesByGrade(grade);
    }

    public Map<String, Object> getStudentDetails(String term, String grade, String section, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Student> studentPage;
        if (grade == null && section == null) {
            studentPage = studentRepository.findAll(pageable);
        } else {
            studentPage = studentRepository.findByGradeAndSectionName(grade, section, pageable);
        }

        List<Student> students = studentPage.getContent();

        if (students == null) {
            students = Collections.emptyList();
        }

        // Fetch all quiz submissions for the students
        List<QuizSubmission> allQuizSubmissions = students.stream()
                .flatMap(student -> quizSubmissionRepository.findByStudentRollNumber(student.getRollNumber()).stream())
                .collect(Collectors.toList());

        // Fetch all OGA submissions for the students
        List<OgaSubmission> allOgaSubmissions = students.stream()
                .flatMap(student -> ogaSubmissionRepository.findByStudentRollNumber(student.getRollNumber()).stream())
                .collect(Collectors.toList());

        // Fetch all assignment submissions for the students
        List<AssignmentSubmission> allAssignmentSubmissions = students.stream()
                .flatMap(student -> assignmentSubmissionRepository.findByStudentRollNumber(student.getRollNumber())
                        .stream())
                .collect(Collectors.toList());

        // Fetch all exam submissions for the students
        List<ExamSubmission> allExamSubmissions = students.stream()
                .flatMap(student -> examSubmissionRepository.findByStudentRollNumber(student.getRollNumber()).stream())
                .collect(Collectors.toList());

        // Calculate average quiz marks
        Map<String, Double> avgQuizMarksMap = allQuizSubmissions.stream()
                .collect(Collectors.groupingBy(
                        QuizSubmission::getStudentRollNumber,
                        Collectors.averagingInt(QuizSubmission::getGainedMarks)));

        // Calculate average OGA marks
        Map<String, Double> avgOgaMarksMap = allOgaSubmissions.stream()
                .collect(Collectors.groupingBy(
                        OgaSubmission::getStudentRollNumber,
                        Collectors.averagingInt(OgaSubmission::getGainedMarks)));

        // Calculate average assignment marks
        Map<String, Double> avgAssignmentMarksMap = allAssignmentSubmissions.stream()
                .collect(Collectors.groupingBy(
                        AssignmentSubmission::getStudentRollNumber,
                        Collectors.averagingDouble(AssignmentSubmission::getObtainedMarks)));

        // Calculate average exam marks
        Map<String, Double> avgExamMarksMap = allExamSubmissions.stream()
                .collect(Collectors.groupingBy(
                        ExamSubmission::getStudentRollNumber,
                        Collectors.averagingInt(ExamSubmission::getGainedMarks)));

        // Aggregate the data
        List<Map<String, Object>> studentDetailsList = students.stream().map(student -> {
            Map<String, Object> studentDetails = new HashMap<>();
            studentDetails.put("studentName", student.getStudentName());
            studentDetails.put("rollNumber", student.getRollNumber());

            double avgQuizMarks = avgQuizMarksMap.getOrDefault(student.getRollNumber(), 0.0);
            double avgOgaMarks = avgOgaMarksMap.getOrDefault(student.getRollNumber(), 0.0);
            double avgAssignmentMarks = avgAssignmentMarksMap.getOrDefault(student.getRollNumber(), 0.0);
            double avgExamMarks = avgExamMarksMap.getOrDefault(student.getRollNumber(), 0.0);

            studentDetails.put("avgQuizGainedMarks", avgQuizMarks);
            studentDetails.put("avgOgaGainedMarks", avgOgaMarks);
            studentDetails.put("avgAssignmentGainedMarks", avgAssignmentMarks);
            studentDetails.put("avgExamGainedMarks", avgExamMarks);

            return studentDetails;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("studentDetails", studentDetailsList);
        response.put("totalPages", studentPage.getTotalPages());
        response.put("totalElements", studentPage.getTotalElements());
        response.put("currentPage", studentPage.getNumber());
        response.put("pageSize", studentPage.getSize());

        return response;
    }

    public StudentListResponse getStudentBySearch(String rollNumber) {
        Utility.printDebugLogs("student roll number " + rollNumber);
        StudentListResponse studentListResponse;
        try {
            List<Student> students = studentRepository.SearchByRollNumber(rollNumber);
            if (students.isEmpty()) {
                List<Student> emptyList = new ArrayList<>();
                studentListResponse = StudentListResponse.builder()
                        .studentList(emptyList)
                        .messageStatus("Success")
                        .build();
                return studentListResponse;

            }
            studentListResponse = StudentListResponse.builder().studentList(students).messageStatus("Success").build();
            Utility.printDebugLogs("studentListResponse is " + studentListResponse);
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

    @Transactional
    public StudentListResponse deleteMultipleStudents(StudentIdsRequest studentIds) {
        Utility.printDebugLogs("Student deletion request for student IDs: " + studentIds);
        List<Long> ids = studentIds.getStudentIds();

        boolean anyNotFound = false;

        try {
            for (Long studentId : ids) {
                Utility.printDebugLogs("Processing deletion for Student ID: " + studentId);
                Optional<Student> existingStudent = studentRepository.findById(studentId);

                if (existingStudent.isPresent()) {
                    // Delete associated courses
                    List<StudentCourse> studentCourses = studentCourseRepository
                            .findAllByStudentId(studentId);
                    if (!studentCourses.isEmpty()) {
                        studentCourseRepository.deleteAll(studentCourses);
                    }

                    studentRepository.deleteById(studentId);
                    Utility.printDebugLogs("Student deleted successfully: " + existingStudent.get());
                } else {
                    Utility.printErrorLogs("No student found with ID: " + studentId);
                    anyNotFound = true;
                }
            }

            if (!anyNotFound) {
                return StudentListResponse.builder()
                        .messageStatus("Success")
                        .build();
            } else {
                String message = "Not Found";
                Utility.printErrorLogs(message);
                return StudentListResponse.builder()
                        .messageStatus(message)
                        .build();
            }
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("IllegalArgumentException: " + e.toString());
            return StudentListResponse.builder()
                    .messageStatus("Failure: IllegalArgumentException - " + e.getMessage())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs("Exception: " + e.toString());
            return StudentListResponse.builder()
                    .messageStatus("Failure: " + e.getMessage())
                    .build();
        }
    }

}
