package com.rtechnologies.soies.service;

import com.opencsv.exceptions.CsvException;
import com.rtechnologies.soies.model.*;
import com.rtechnologies.soies.model.association.TeacherCampusSectionGradeBranch;
import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.association.TeacherSection;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.ArrayList;
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
    @Autowired
    private TeacherCampusSectionGradeBranchRepo teacherCampusSectionGradeBranchRepo;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private CampusRepository campusRepository;


    public TeacherResponse createTeacher(CreateTeacherDTO teacherDTO) {
        Utility.printDebugLogs("Teacher creation request: " + teacherDTO.toString());
        System.out.println("Teacher creation request: " + teacherDTO.toString());
        TeacherResponse teacherResponse = null;

        try {
            if (teacherDTO == null) {
                Utility.printDebugLogs("Teacher object is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            Optional<Teacher> teacherOptional = teacherRepository.findByEmail(teacherDTO.getEmail());
            if (teacherOptional.isPresent()) {
                Utility.printErrorLogs("Teacher already exists with email: " + teacherDTO.getEmail());
                throw new IllegalArgumentException("Account already exists");
            }

            String hashedPassword = new BCryptPasswordEncoder().encode(teacherDTO.getPassword());
            teacherDTO.setPassword(hashedPassword);

            String formattedGrade = formatGradeSection(teacherDTO.getTeacherSectionList());

            Teacher savingTeacher = mapToTeacher(teacherDTO, formattedGrade);
            Teacher savedTeacher = teacherRepository.save(savingTeacher);
            Utility.printDebugLogs("Saved teacher: " + savedTeacher.toString());
            Long campusId = getOrCreateCampus(savedTeacher.getCampusName());

            for (TeacherSection teacherSection : teacherDTO.getTeacherSectionList()) {
                String grade = teacherSection.getGrade();
                String[] sections = teacherSection.getSection().split(",");

                for (String section : sections) {
                    Long sectionId = getOrCreateSection(campusId, section.trim(), grade);
                    saveTeacherCampusSectionGradeAssociation(savedTeacher.getTeacherId(), sectionId);

                    // Process courses for each section
                    List<Course> courses = getCoursesForGrade(grade);
                    for (Course course : courses) {
                        Long courseId = getOrCreateCourse(course.getCourseName(), grade);
                        saveTeacherCourseAssociation(savedTeacher.getTeacherId(), courseId);
                    }
                }
            }

            // Prepare response
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
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error: " + e);
            teacherResponse = TeacherResponse.builder()
                    .messageStatus("Failure")
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse);
            return teacherResponse;
        }
    }

    private Long getOrCreateCampus(String campusName) {
        Optional<Campus> campusData = campusRepository.findByCampusName(campusName);
        Long campusId;
        if (!campusData.isPresent()) {
            Campus campus = Campus.builder().campusName(campusName).build();
            campus = campusRepository.save(campus);
            campusId = campus.getId();
            System.err.println("Campus does not exist, created new campus with ID: " + campusId);
        } else {
            campusId = campusData.get().getId();
            System.err.println("Campus exists with ID: " + campusId);
        }
        return campusId;
    }

    private Long getOrCreateSection(Long campusId, String sectionName, String grade) {
        Optional<Section> sectionData = sectionRepository.findByCampusIdAndSectionNameIgnoreCaseAndGrade(campusId, sectionName, grade);
        Long sectionId;
        if (!sectionData.isPresent()) {
            Section newSection = Section.builder().campusId(campusId).grade(grade).sectionName(sectionName).build();
            newSection = sectionRepository.save(newSection);
            sectionId = newSection.getId();
            System.err.println("Section does not exist, created new section with ID: " + sectionId);
        } else {
            sectionId = sectionData.get().getId();
            System.err.println("Section exists with ID: " + sectionId);
        }
        return sectionId;
    }

    private void saveTeacherCampusSectionGradeAssociation(Long teacherId, Long sectionId) {
        Optional<TeacherCampusSectionGradeBranch> existingAssociation = teacherCampusSectionGradeBranchRepo.findByTeacheIdFkAndSectionIdFk(teacherId, sectionId);
        if (!existingAssociation.isPresent()) {
            teacherCampusSectionGradeBranchRepo.save(TeacherCampusSectionGradeBranch.builder()
                    .teacheIdFk(teacherId)
                    .sectionIdFk(sectionId)
                    .build());
            System.err.println("Added new section to teacher with ID: " + teacherId);
        } else {
            System.err.println("Teacher already has this section assigned.");
        }
    }

    private Long getOrCreateCourse(String courseName, String grade) {
        Optional<Course> courseData = courseRepository.findByCourseNameIgnoreCaseAndGrade(courseName, grade);
        Long courseId;
        if (!courseData.isPresent()) {
            Course newCourse = Course.builder().courseName(courseName).grade(grade).build();
            newCourse = courseRepository.save(newCourse);
            courseId = newCourse.getCourseId();
            System.err.println("Course does not exist, created new course with ID: " + courseId);
        } else {
            courseId = courseData.get().getCourseId();
            System.err.println("Course exists with ID: " + courseId);
        }
        return courseId;
    }

    private void saveTeacherCourseAssociation(Long teacherId, Long courseId) {
        Optional<TeacherCourse> existingAssociation = teacherCourseRepository.findByTeacherIdAndCourseId(teacherId, courseId);
        if (!existingAssociation.isPresent()) {
            teacherCourseRepository.save(TeacherCourse.builder().teacherId(teacherId).courseId(courseId).build());
            System.err.println("Added new course to teacher with ID: " + teacherId);
        } else {
            System.err.println("Teacher already has this course assigned.");
        }
    }

    private List<Course> getCoursesForGrade(String grade) {
        return courseRepository.findCoursesByGrade(grade);
    }

    private String formatGradeSection(List<TeacherSection> sections) {
        Map<String, List<String>> gradeToSectionsMap = new LinkedHashMap<>();

        for (TeacherSection section : sections) {
            String grade = section.getGrade();
            String sectionName = section.getSection();

            gradeToSectionsMap
                    .computeIfAbsent(grade, k -> new ArrayList<>())
                    .add(sectionName);
        }

        StringBuilder formattedGrade = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : gradeToSectionsMap.entrySet()) {
            String grade = entry.getKey();
            List<String> sectionNames = entry.getValue();
            formattedGrade.append(grade).append("(")
                    .append(String.join(",", sectionNames))
                    .append("), ");
        }
        return formattedGrade.toString().replaceAll(", $", "");
    }

    public static Teacher mapToTeacher(CreateTeacherDTO createTeacherDTO, String formattedGrade) {
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
                .grade(formattedGrade) // Set the formatted grade
                .build();
    }

    @Transactional
    public TeacherResponse deleteTeacher(Long teacherId) {
        Utility.printDebugLogs("Teacher deletion request ID: " + teacherId);
        TeacherResponse teacherResponse;

        try {
            if (teacherId == null || teacherId <= 0) {
                Utility.printErrorLogs("Invalid teacherId for deletion: " + teacherId);
                throw new IllegalArgumentException("Invalid Teacher ID for deletion");
            }

            Optional<Teacher> existingTeacher = teacherRepository.findById(teacherId);
            if (existingTeacher.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with ID: " + teacherId);
                throw new NotFoundException("Teacher not found with ID: " + teacherId);
            }

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


    public TeacherResponse updateTeacher(UpdateTeacherDTO teacher) {
        Utility.printDebugLogs("Teacher update request: " + teacher.toString());
        TeacherResponse teacherResponse;

        try {
            if (teacher == null || teacher.getTeacherId() == null || teacher.getTeacherId() <= 0) {
                Utility.printErrorLogs("Invalid teacher or teacher ID: " + teacher.getTeacherId());
                throw new IllegalArgumentException("Invalid teacher or teacher ID: " + teacher.getTeacherId());
            }

            Optional<Teacher> existingTeacherOptional = teacherRepository.findById(teacher.getTeacherId());
            if (existingTeacherOptional.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with ID: " + teacher.getTeacherId());
                throw new NotFoundException("Teacher not found with ID: " + teacher.getTeacherId());
            }

            Teacher existingTeacher = existingTeacherOptional.get();

            existingTeacher.setCampusName(teacher.getCampusName());
            existingTeacher.setEmployeeName(teacher.getEmployeeName());
            existingTeacher.setEmail(teacher.getEmail());
            existingTeacher.setDateOfBirth(teacher.getDateOfBirth());
            existingTeacher.setGender(teacher.getGender());
            existingTeacher.setJoiningDate(teacher.getJoiningDate());
            existingTeacher.setPhoneNumber(teacher.getPhoneNumber());
            existingTeacher.setAddress(teacher.getAddress());

            String formattedGrade = formatGradeSection(teacher.getTeacherSectionList());
            existingTeacher.setGrade(formattedGrade);
            Teacher updatedTeacher = teacherRepository.save(existingTeacher);
            List<TeacherSection> existingSections = teacherSectionRepository.findByTeacherId(updatedTeacher.getTeacherId());

            Map<Long, TeacherSection> existingSectionsMap = existingSections.stream()
                    .collect(Collectors.toMap(TeacherSection::getId, section -> section));

            for (TeacherSection newSection : teacher.getTeacherSectionList()) {
                if (newSection.getId() != null && existingSectionsMap.containsKey(newSection.getId())) {
                    TeacherSection existingSection = existingSectionsMap.get(newSection.getId());
                    existingSection.setSection(newSection.getSection());
                    existingSection.setGrade(newSection.getGrade());
                    existingSection.setTeacherId(updatedTeacher.getTeacherId());
                    teacherSectionRepository.save(existingSection);
                } else {
                    newSection.setTeacherId(updatedTeacher.getTeacherId());
                    teacherSectionRepository.save(newSection);
                }
            }
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
                    .messageStatus("Teacher not found: " + e.getMessage())
                    .build();
            Utility.printErrorLogs("Teacher Response: " + teacherResponse.toString());
            return teacherResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error updating teacher: " + e.getMessage());
            teacherResponse = TeacherResponse.builder()
                    .messageStatus("Invalid input: " + e.getMessage())
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
                .teacher_Id(teacher.getTeacherId())
                .Campus_Name(teacher.getCampusName())
                .Employee_Name(teacher.getEmployeeName())
                .email(teacher.getEmail())
                .grade(teacher.getGrade())
                .address(teacher.getAddress())
                .Date_Of_Birth(teacher.getDateOfBirth())
                .Phone_number(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .Joining_date(teacher.getJoiningDate())
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
            // Validate email
            if (email == null || email.isEmpty()) {
                Utility.printErrorLogs("Invalid teacher Email for fetching teacher: " + email);
                teacherResponse.setMessageStatus("Failure");
                return teacherResponse;
            }

            // Fetch teacher by email
            Optional<Teacher> optionalTeacher = teacherRepository.findByEmail(email);
            if (optionalTeacher.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with Email: " + email);
                teacherResponse.setMessageStatus("Failure");
                return teacherResponse;
            }

            Teacher teacher = optionalTeacher.get();

            // Fetch teacher-section associations
            List<TeacherCampusSectionGradeBranch> teacherSections = teacherCampusSectionGradeBranchRepo.findByTeacheIdFk(teacher.getTeacherId());

            // Extract section IDs from teacher-section associations
            List<Long> sectionIds = teacherSections.stream()
                    .map(TeacherCampusSectionGradeBranch::getSectionIdFk)
                    .collect(Collectors.toList());

            // Fetch section details using section IDs
            List<Section> sections = sectionRepository.findByIdIn(sectionIds);

            // Map section details to TeacherSection objects
            List<TeacherSection> teacherSectionList = sections.stream()
                    .map(section -> TeacherSection.builder()
                            .teacherId(teacher.getTeacherId())
                            .section(section.getSectionName())  // Section name
                            .grade(section.getGrade())   // Grade
                            .build())
                    .collect(Collectors.toList());

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
                    .teacherSections(teacherSectionList) // Use the TeacherSection list here
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Get teacher by Email response: " + teacherResponse);
            return teacherResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error fetching teacher by Email: " + e.getMessage());
            teacherResponse.setMessageStatus(e.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error fetching teacher by Email: " + e.getMessage());
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
        Pageable pageable = PageRequest.of(page, size);
        Page<TeacherProjection> listTeacher;

        // Determine the correct query to use based on the provided parameters
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

        // Process the list of teachers and their grades
        List<TeacherProjection> teachers = listTeacher.getContent();
        List<TeacherDTO> teacherDTOList = new ArrayList<>();

        for (TeacherProjection teacher : teachers) {
            String gradeData = teacher.getGrade();
            List<TeacherSectionDTO> teacherSectionList = new ArrayList<>();

            if (gradeData != null && !gradeData.isEmpty()) {
                String[] gradeEntries = gradeData.split("(?i)(?=Grade)"); // Use "(?i)" for case-insensitive matching

                for (String entry : gradeEntries) {
                    String[] parts = entry.split("\\(");
                    if (parts.length == 2) {
                        String gradeName = parts[0].trim();
                        String sections = parts[1].replace(")", "").trim();
                        String[] sectionNames = sections.split(",");

                        for (String sectionName : sectionNames) {
                            TeacherSectionDTO sectionDTO = new TeacherSectionDTO();
                            sectionDTO.setGrade(gradeName);
                            sectionDTO.setSection(sectionName.trim());
                            teacherSectionList.add(sectionDTO);
                        }
                    }
                }
            }

            TeacherDTO dto = TeacherDTO.builder()
                    .teacher_Id(teacher.getTeacher_id()) // Ensure this is correctly fetched
                    .Campus_Name(teacher.getCampus_Name())
                    .Employee_Name(teacher.getEmployee_Name())
                    .email(teacher.getEmail())
                    .Date_Of_Birth(teacher.getDate_Of_Birth())
                    .gender(teacher.getGender())
                    .Joining_date(teacher.getJoining_date())
                    .Phone_number(teacher.getPhone_number())
                    .address(teacher.getAddress())
                    .userName(teacher.getTeacherName())
                    .gender(teacher.getGender())
                    .grade(teacher.getGrade()) // Include grade as well
                    .teacherSectionList(teacherSectionList) // Set the teacherSectionList
                    .build();

            teacherDTOList.add(dto);
        }

        // Convert the list of TeacherDTO to a Page for consistency
        Page<TeacherDTO> teacherDTOPage = new PageImpl<>(teacherDTOList, pageable, listTeacher.getTotalElements());

        // Set the response
        teacherResponse.setTeacherJoinDataPage(teacherDTOPage); // Set paginated teacher data
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

