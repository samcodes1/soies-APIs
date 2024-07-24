package com.rtechnologies.soies.service;

import com.opencsv.exceptions.CsvException;
import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.association.TeacherSection;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.TeacherCourseRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.repository.TeacherSectionRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherSectionRepository teacherSectionRepository;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExcelParser excelParser;

    public TeacherResponse createTeacher(CreateTeacherDTO teacher) {
        Utility.printDebugLogs("Teacher creation request: " + teacher.toString());
        System.out.println("Teacher creation request: " + teacher.toString());
        TeacherResponse teacherResponse = null;
        try {
            if (teacher == null) {
                Utility.printDebugLogs("Teacher object is null: " + teacher.toString());
                throw new IllegalArgumentException("Corrupt data receive");
            }

            Optional<Teacher> teacherOptional = teacherRepository.findByEmail(teacher.getEmail());
            if (teacherOptional.isPresent()) {
                Utility.printErrorLogs("Teacher already exists with following email: "
                        + teacher.getEmail());
                throw new IllegalArgumentException("Account already exists");
            }

            // Hash the password using BCryptPasswordEncoder
            String hashedPassword = new BCryptPasswordEncoder().encode(teacher.getPassword());

            // Set the hashed password to the teacher object
            teacher.setPassword(hashedPassword);

            Teacher savingTeacher = mapToTeacher(teacher);
            // Save the teacher with the hashed password
            Teacher savedTeacher = teacherRepository.save(savingTeacher);
            Utility.printDebugLogs("Saved teacher: " + savedTeacher.toString());


            // for(TeacherSection teacherSection : teacher.getTeacherSectionList()) {
            //     teacherSection.setTeacherId(savedTeacher.getTeacherId());
            // }

            int length = teacher.getTeacherSectionList().size();

            for (int i = 0; i < length; i++) {
                teacher.getTeacherSectionList().get(i).setTeacherId(savedTeacher.getTeacherId());
            }

            teacherSectionRepository.saveAll(teacher.getTeacherSectionList());

            teacherResponse = TeacherResponse.builder()
                    .teacherId(savedTeacher.getTeacherId())
                    .campusName(savedTeacher.getCampusName())
                    .employeeName(savedTeacher.getEmployeeName())
                    .email(savedTeacher.getEmail())
                    .dateOfBirth(savedTeacher.getDateOfBirth())
                    .gender(savedTeacher.getGender())
                    .joiningDate(savedTeacher.getJoiningDate())
                    .phoneNumber(savedTeacher.getPhoneNumber())
                    .address(savedTeacher.getAddress())
                    .messageStatus("Success").build();

            Utility.printDebugLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error: " + e);
            teacherResponse = TeacherResponse.builder()
                    .messageStatus("Failure").build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse);
            return teacherResponse;
        }
    }

    public static Teacher mapToTeacher(CreateTeacherDTO createTeacherDTO) {
        return Teacher.builder()
                .campusName(createTeacherDTO.getCampusName())
                .employeeName(createTeacherDTO.getEmployeeName())
                .email(createTeacherDTO.getEmail())
                .password(createTeacherDTO.getPassword())
                .dateOfBirth(createTeacherDTO.getDateOfBirth())
                .gender(createTeacherDTO.getGender())
                .joiningDate(createTeacherDTO.getJoiningDate())
                .phoneNumber(createTeacherDTO.getPhoneNumber())
                .address(createTeacherDTO.getAddress())
                // You may need to handle teacherSectionList mapping here if required
                .build();
    }

    @Transactional
    public TeacherResponse deleteTeacher(Long teacherId) {
        Utility.printDebugLogs("Teacher deletion request ID: " + teacherId);
        TeacherResponse teacherResponse;

        try {
            // Validate teacherId
            if (teacherId == null || teacherId <= 0) {
                Utility.printErrorLogs("Invalid teacherId for deletion: " + teacherId);
                throw new IllegalArgumentException("Invalid Teacher ID for deletion");
            }

            // Check if the teacher exists
            Optional<Teacher> existingTeacher = teacherRepository.findById(teacherId);
            if (existingTeacher.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with ID: " + teacherId);
                throw new NotFoundException("Teacher not found with ID: " + teacherId);
            }

            // Delete the teacher
            teacherRepository.deleteById(teacherId);

            Utility.printDebugLogs("Teacher deleted successfully. ID: " + teacherId);
            teacherResponse = TeacherResponse.builder()
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error deleting teacher: " + e.getMessage());
            teacherResponse = TeacherResponse.builder()
                    .messageStatus(e.toString())
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error deleting teacher: " + e.getMessage());
            teacherResponse = TeacherResponse.builder()
                    .messageStatus("Failure")
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;
        }
    }


    public TeacherResponse updateTeacher(Teacher teacher) {
        Utility.printDebugLogs("Teacher update request: " + teacher.toString());
        TeacherResponse teacherResponse;

        try {
            // Validate teacher and teacherId
            if (teacher == null || teacher.getTeacherId() == null || teacher.getTeacherId() <= 0) {
                Utility.printErrorLogs("Invalid teacher or teacher ID: " + teacher.getTeacherId());
                throw new IllegalArgumentException("Invalid teacher or teacher ID: " + teacher.getTeacherId());
            }

            // Check if the teacher with the specified ID exists
            Optional<Teacher> existingTeacher = teacherRepository.findById(teacher.getTeacherId());
            if (existingTeacher.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with ID: " + teacher.getTeacherId());
                throw new NotFoundException("Teacher not found with ID: " + teacher.getTeacherId());
            }

            // Update the teacher details
            Teacher updatedTeacher = teacherRepository.save(teacher);

            // Build the response object
            teacherResponse = TeacherResponse.builder()
                    .teacherId(updatedTeacher.getTeacherId())
                    .campusName(updatedTeacher.getCampusName())
                    .employeeName(updatedTeacher.getEmployeeName())
                    .email(updatedTeacher.getEmail())
                    .dateOfBirth(updatedTeacher.getDateOfBirth())
                    .gender(updatedTeacher.getGender())
                    .joiningDate(updatedTeacher.getJoiningDate())
                    .phoneNumber(updatedTeacher.getPhoneNumber())
                    .address(updatedTeacher.getAddress())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Teacher updated successfully. Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (NotFoundException e) {
            Utility.printErrorLogs("Error updating teacher: " + e.getMessage());
            teacherResponse = TeacherResponse.builder()
                    .messageStatus(e.toString())
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error updating teacher: " + e.getMessage());
            teacherResponse = TeacherResponse.builder()
                    .messageStatus(e.toString())
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error updating teacher: " + e.getMessage());
            teacherResponse = TeacherResponse.builder()
                    .messageStatus("Failure")
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;
        }
    }


//    public void changePassword(Long teacherId, String newPassword) {
//        try {
//            if (teacherId == null || teacherId <= 0 || newPassword == null || newPassword.isEmpty()) {
//                throw new IllegalArgumentException("Invalid teacherId or newPassword");
//            }
//
//            Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
//            if (optionalTeacher.isPresent()) {
//                Teacher teacher = optionalTeacher.get();
//                teacher.setPassword(newPassword);
//                teacherRepository.save(teacher);
//            } else {
//                throw new TeacherNotFoundException("Teacher not found with ID: " + teacherId);
//            }
//        } catch (Exception e) {
//            throw new TeacherServiceException("Error changing password for teacher with ID: " + teacherId, e);
//        }
//    }

    public TeacherListResponse getAllTeachersByCampusName(String campusName, int page, int size) {
        Utility.printDebugLogs("Get all teachers by campusName request: " + campusName);
        TeacherListResponse teacherListResponse = new TeacherListResponse();

        try {
            // Validate campusName
            if (campusName == null || campusName.isEmpty()) {
                Utility.printErrorLogs("Invalid campusName for fetching teachers");
                teacherListResponse.setMessageStatus("Failure");
                return teacherListResponse;
            }

            // Fetch teachers by campusName
            Pageable pageable = PageRequest.of(page, size);
            List<Teacher> teachers = teacherRepository.findByCampusNamePage(campusName, pageable);

            if (teachers.isEmpty() || teachers.size() < 0) {
                Utility.printErrorLogs("No record found of teachers for campus name: " + campusName);
                teacherListResponse.setMessageStatus("Failure");
                return teacherListResponse;
            }

            Utility.printDebugLogs("Teachers record " + teachers + " by campusName: " + campusName);

            teacherListResponse = TeacherListResponse.builder()
                    .teacherList(teachers)
                    .messageStatus("Success").build();

            Utility.printDebugLogs("Teacher list response " + teacherListResponse + " by campusName: " + campusName);
            return teacherListResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error fetching teachers by campusName: " + e.getMessage());
            teacherListResponse.setMessageStatus(e.toString());
            return teacherListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error fetching teachers by campusName: " + e.getMessage());
            teacherListResponse.setMessageStatus("Failure");
            return teacherListResponse;
        }
    }


    public TeacherListResponse getAllTeachers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Teacher> teachersPage = teacherRepository.findAll(pageable);
        List<Teacher> teachers = teachersPage.getContent();

        Map<Long, List<TeacherSection>> teacherSectionsMap = fetchTeacherSectionsMap();
        Map<Long, List<Course>> teacherCoursesMap = fetchTeacherCoursesMap();

        List<TeacherDTO> teacherDTOs = teachers.stream()
                .map(teacher -> mapToTeacherDTO(teacher, teacherSectionsMap.get(teacher.getTeacherId()), teacherCoursesMap.get(teacher.getTeacherId())))
                .collect(Collectors.toList());

        return TeacherListResponse.builder()
                .teacherCompleteList(teacherDTOs)
                .messageStatus("Success")
                .build();
    }

    private TeacherDTO mapToTeacherDTO(Teacher teacher, List<TeacherSection> sections, List<Course> courses) {
        return TeacherDTO.builder()
                .teacherId(teacher.getTeacherId())
                .campusName(teacher.getCampusName())
                .employeeName(teacher.getEmployeeName())
                .email(teacher.getEmail())
                .grade(teacher.getGrade())
                .address(teacher.getAddress())
                .dateOfBirth(teacher.getDateOfBirth())
                .phoneNumber(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .joiningDate(teacher.getJoiningDate())
                .userName(teacher.getUserName())
                .sections(sections != null ? sections.stream()
                        .map(TeacherSection::getSection)
                        .collect(Collectors.toList()) : null)
                .courses(courses != null ? courses.stream()
                        .map(Course::getCourseName)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private Map<Long, List<TeacherSection>> fetchTeacherSectionsMap() {
        return teacherSectionRepository.findAll().stream()
                .collect(Collectors.groupingBy(TeacherSection::getTeacherId));
    }

    private Map<Long, List<Course>> fetchTeacherCoursesMap() {
        List<TeacherCourse> allTeacherCourses = teacherCourseRepository.findAll();
        List<Course> allCourses = courseRepository.findAll();
        Map<Long, Course> courseMap = allCourses.stream()
                .collect(Collectors.toMap(Course::getCourseId, course -> course));

        return allTeacherCourses.stream()
                .collect(Collectors.groupingBy(TeacherCourse::getTeacherId,
                        Collectors.mapping(tc -> courseMap.get(tc.getCourseId()), Collectors.toList())));
    }

    public TeacherWithSectionResponse getTeacherByEmail(String email) {
        Utility.printDebugLogs("Get teacher by Email request: " + email);
        TeacherWithSectionResponse teacherResponse = new TeacherWithSectionResponse();

        try {
            // Validate teacherId
            if (email == null || email.isEmpty()) {
                Utility.printErrorLogs("Invalid teacher Email for fetching teacher: " + email);
                teacherResponse.setMessageStatus("Failure");
                return teacherResponse;
            }

            // Fetch teacher by ID
            Optional<Teacher> optionalTeacher = teacherRepository.findByEmail(email);
            if (optionalTeacher.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with Email: " + email);
                teacherResponse.setMessageStatus("Failure");
                return teacherResponse;
            }

            List<TeacherSection> teacherSections = teacherSectionRepository.findByTeacherId(optionalTeacher.get().getTeacherId());
            Teacher teacher = optionalTeacher.get();
            teacherResponse = TeacherWithSectionResponse.builder()
                    .teacherId(teacher.getTeacherId())
                    .campusName(teacher.getCampusName())
                    .employeeName(teacher.getEmployeeName())
                    .email(teacher.getEmail())
                    .dateOfBirth(teacher.getDateOfBirth())
                    .gender(teacher.getGender())
                    .joiningDate(teacher.getJoiningDate())
                    .phoneNumber(teacher.getPhoneNumber())
                    .address(teacher.getAddress())
                    .teacherSections(teacherSections)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Get teacher by ID response: " + teacherResponse);
            return teacherResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error fetching teacher by ID: " + e.getMessage());
            teacherResponse.setMessageStatus(e.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error fetching teacher by ID: " + e.getMessage());
            teacherResponse.setMessageStatus("Failure");
            return teacherResponse;
        }
    }

    public TeacherSectionResponse getTeacherSection(Long teacherId) {
        TeacherSectionResponse teacherResponse = new TeacherSectionResponse();
        // Fetch teacher by ID
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
        if (optionalTeacher.isEmpty()) {
            Utility.printDebugLogs("Teacher not found with ID: " + teacherId);
            teacherResponse.setMessageStatus("Teacher not found with ID: " + teacherId);
            return teacherResponse;
        }

        List<TeacherSection> teacherSections = teacherSectionRepository.findByTeacherId(teacherId);
        if (teacherSections.isEmpty()) {
            Utility.printDebugLogs("No sections allocated to teacher");
            teacherResponse.setMessageStatus("No sections allocated to teacher");
            return teacherResponse;
        }

        teacherResponse.setTeacherSections(teacherSections);
        teacherResponse.setMessageStatus("Success");

        return teacherResponse;
    }

    public TeacherListResponse getTeachersByCourseGradeSection(String campusName, String courseName, String grade, String section, int page, int size) {
        TeacherListResponse teacherResponse = new TeacherListResponse();
        // Fetch teacher by ID
        Pageable pageable = PageRequest.of(page, size);
        Page<TeacherProjection> listTeacher = null;
        if (courseName == null && grade == null && section == null) {
            listTeacher = teacherRepository.findByCampusName(campusName, pageable);
        } else if (courseName == null && grade == null && section != null) {
            listTeacher = teacherRepository.findByCampusNamesection(campusName, section, pageable);
        } else if (courseName != null && grade == null && section == null) {
            listTeacher = teacherRepository.findByCampusNameCourse(campusName, courseName, pageable);
        } else if (courseName == null && grade != null && section == null) {
            listTeacher = teacherRepository.findByCampusNameGrade(campusName, grade, pageable);
        } else if (courseName == null && grade != null && section != null) {
            listTeacher = teacherRepository.findByCampusNamesectionOrGrade(campusName, section, grade, pageable);
        } else if (courseName != null && grade == null && section != null) {
            listTeacher = teacherRepository.findByCampusNameCourseAndSection(campusName, courseName, section, pageable);
        } else if (courseName != null && grade != null && section == null) {
            listTeacher = teacherRepository.findByCampusNameCourseAndGrade(campusName, courseName, grade, pageable);
        } else {
            listTeacher = teacherRepository.findByCampusNameCourseGradeSection(campusName, section, courseName, grade, pageable);
        }


        // List<Teacher> listTeacher = teacherRepository.findByCampusNameCourseGradeSection( campusName, section, courseName, grade, pageable);
        // if (listTeacher==null || listTeacher.isEmpty()) {
        //     Utility.printDebugLogs("Teacher not found");
        //     teacherResponse.setMessageStatus("Teacher not found ");
        //     return teacherResponse;
        // }

        // List<TeacherSection> teacherSections = teacherSectionRepository.findByTeacherId(teacherId);
        // if(teacherSections.isEmpty()) {
        //     Utility.printDebugLogs("No sections allocated to teacher");
        //     teacherResponse.setMessageStatus("No sections allocated to teacher");
        //     return teacherResponse;
        // }

        teacherResponse.setTeacherJoinDataPage(listTeacher);
        teacherResponse.setMessageStatus("Success");

        return teacherResponse;
    }

    @Transactional
    public TeacherResponse saveTrachersFromFile(MultipartFile file) throws IOException {
        Runnable runnable = () -> {
            List<Teacher> teachers;
            try {
                teachers = excelParser.parseTeacherFile(file);
                System.out.println("TEACHERDATA:>> " + teachers.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start(); // Start the thread

        TeacherResponse teacherListResponse = TeacherResponse.builder()
                .teacherId(null)
                .campusName(null)
                .employeeName(null)
                .email(null)
                .dateOfBirth(null)
                .gender(null)
                .joiningDate(null)
                .phoneNumber(null)
                .address(null)
                .messageStatus("Success")
                .build();
        return teacherListResponse;

        // if(Utility.isCSV(file)){

        // }
        // List<Teacher> teachers = excelParser.parseTeacherExcelFile(file.getInputStream());
        // teacherRepository.saveAll(teachers);
        // TeacherResponse teacherResponse = TeacherResponse.builder()
        //             .teacherId(null)
        //             .campusName(null)
        //             .employeeName(null)
        //             .email(null)
        //             .dateOfBirth(null)
        //             .gender(null)
        //             .joiningDate(null)
        //             .phoneNumber(null)
        //             .address(null)
        //             .messageStatus("Success").build();
        // return teacherResponse;
    }
}

