package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.TeacherListResponse;
import com.rtechnologies.soies.model.dto.TeacherResponse;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public TeacherResponse createTeacher(Teacher teacher) {
        Utility.printDebugLogs("Teacher creation request: "+teacher.toString());
        System.out.println("Teacher creation request: "+teacher.toString());
        TeacherResponse teacherResponse = null;
        try {
            if (teacher == null) {
                Utility.printDebugLogs("Teacher object is null: "+ teacher.toString());
                throw new IllegalArgumentException("Corrupt data receive");
            }

            Optional<Teacher> teacherOptional = teacherRepository.findByEmail(teacher.getEmail());
            if(teacherOptional.isPresent()) {
                Utility.printErrorLogs("Teacher already exists with following email: "
                        + teacher.getEmail());
                throw new IllegalArgumentException("Account already exists");
            }

            // Hash the password using BCryptPasswordEncoder
            String hashedPassword = new BCryptPasswordEncoder().encode(teacher.getPassword());

            // Set the hashed password to the teacher object
            teacher.setPassword(hashedPassword);

            // Save the teacher with the hashed password
            Teacher savedTeacher = teacherRepository.save(teacher);
            Utility.printDebugLogs("Saved teacher: "+ savedTeacher.toString());
            System.out.println("Teacher saved: " + savedTeacher.toString());

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

            System.out.println("Teacher respose: " + teacherResponse);
            Utility.printDebugLogs("Teacher Response: "+ teacherResponse.toString());
            return teacherResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error: "+ e);
             teacherResponse = TeacherResponse.builder()
                    .messageStatus("Failure").build();
            Utility.printErrorLogs("Teacher Response: "+ teacherResponse);
            return teacherResponse;
        }
    }

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

        }  catch (NotFoundException e) {
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

    public TeacherListResponse getAllTeachersByCampusName(String campusName) {
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
            List<Teacher> teachers = teacherRepository.findByCampusName(campusName);

            if(teachers.isEmpty() || teachers.size() < 0) {
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


    public TeacherListResponse getAllTeachers() {
        Utility.printDebugLogs("Get all teachers request");
        TeacherListResponse teacherListResponse = null;

        try {
            // Fetch all teachers
            List<Teacher> teachers = teacherRepository.findAll();
            teacherListResponse = TeacherListResponse.builder()
                                    .teacherList(teachers)
                                    .messageStatus("Success").build();

            Utility.printDebugLogs("Fetched " + teachers.size() + " teachers");
            return teacherListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error fetching all teachers: " + e.getMessage());
            teacherListResponse = TeacherListResponse.builder()
                    .messageStatus("Failure").build();
            return teacherListResponse;
        }
    }


    public TeacherResponse getTeacherById(Long teacherId) {
        Utility.printDebugLogs("Get teacher by ID request: " + teacherId);
        TeacherResponse teacherResponse = new TeacherResponse();

        try {
            // Validate teacherId
            if (teacherId == null || teacherId <= 0) {
                Utility.printErrorLogs("Invalid teacher ID for fetching teacher: "+teacherId);
                teacherResponse.setMessageStatus("Failure");
                return teacherResponse;
            }

            // Fetch teacher by ID
            Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
            if (optionalTeacher.isEmpty()) {
                Utility.printDebugLogs("Teacher not found with ID: " + teacherId);
                teacherResponse.setMessageStatus("Failure");
                return teacherResponse;
            }

            Teacher teacher = optionalTeacher.get();
            teacherResponse = TeacherResponse.builder()
                    .teacherId(teacher.getTeacherId())
                    .campusName(teacher.getCampusName())
                    .employeeName(teacher.getEmployeeName())
                    .email(teacher.getEmail())
                    .dateOfBirth(teacher.getDateOfBirth())
                    .gender(teacher.getGender())
                    .joiningDate(teacher.getJoiningDate())
                    .phoneNumber(teacher.getPhoneNumber())
                    .address(teacher.getAddress())
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

}

