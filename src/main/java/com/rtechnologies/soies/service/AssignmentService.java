package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.AssignmentListResponse;
import com.rtechnologies.soies.model.dto.AssignmentResponse;
import com.rtechnologies.soies.model.dto.TeacherResponse;
import com.rtechnologies.soies.repository.AssignmentRepository;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    public AssignmentResponse createAssignment(Assignment assignment) {
        Utility.printDebugLogs("Assignment creation request: " + assignment.toString());
        AssignmentResponse assignmentResponse;

        try {
            if (assignment == null) {
                Utility.printDebugLogs("Assignment creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            //Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(assignment.getTeacherId());
            if(teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + assignment.getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + assignment.getTeacherId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(assignment.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + assignment.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + assignment.getCourseId());
            }

            Assignment createdAssignment = assignmentRepository.save(assignment);
            Utility.printDebugLogs("Assignment created successfully: " + createdAssignment);

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(createdAssignment.getAssignmentId())
                    .course(course.get())
                    .teacher(teacher.get())
                    .assignmentTitle(createdAssignment.getAssignmentTitle())
                    .description(createdAssignment.getDescription())
                    .file(createdAssignment.getFile())
                    .dueDate(createdAssignment.getDueDate())
                    .totalMarks(createdAssignment.getTotalMarks())
                    .visibility(createdAssignment.isVisibility())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment response: " + assignmentResponse);
            return assignmentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public AssignmentResponse updateAssignment(Assignment assignment) {
        Utility.printDebugLogs("Assignment update request: " + assignment.toString());
        AssignmentResponse assignmentResponse;

        try {
            if (assignment == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            //Check for assignment
            Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignment.getAssignmentId());
            if(assignmentOptional.isEmpty()){
                throw new IllegalArgumentException("No Assignment found with ID: " + assignment.getAssignmentId());
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(assignment.getTeacherId());
            if (teacher.isEmpty()) {
                throw new IllegalArgumentException("No teacher found with ID: " + assignment.getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(assignment.getCourseId());
            if (course.isEmpty()) {
                throw new IllegalArgumentException("No course found with ID: " + assignment.getCourseId());
            }

            Assignment updatedAssignment = assignmentRepository.save(assignment);
            Utility.printDebugLogs("Assignment updated successfully: " + updatedAssignment);

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(updatedAssignment.getAssignmentId())
                    .course(course.get())
                    .teacher(teacher.get())
                    .assignmentTitle(updatedAssignment.getAssignmentTitle())
                    .description(updatedAssignment.getDescription())
                    .file(updatedAssignment.getFile())
                    .dueDate(updatedAssignment.getDueDate())
                    .totalMarks(updatedAssignment.getTotalMarks())
                    .visibility(updatedAssignment.isVisibility())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment response: " + assignmentResponse);
            return assignmentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public AssignmentResponse deleteAssignment(Long assignmentId) {
        Utility.printDebugLogs("Assignment deletion request: " + assignmentId);
        AssignmentResponse assignmentResponse;

        try {
            Optional<Assignment> existingAssignment = assignmentRepository.findById(assignmentId);

            if (existingAssignment.isEmpty()) {
                throw new IllegalArgumentException("No assignment found with ID: " + assignmentId);
            }

            assignmentRepository.deleteById(assignmentId);
            Utility.printDebugLogs("Assignment deleted successfully: " + existingAssignment.get());

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(existingAssignment.get().getAssignmentId())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment response: " + assignmentResponse);
            return assignmentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public AssignmentListResponse getAllAssignmentsByTeacherId(Long teacherId) {
        Utility.printDebugLogs("Get all assignments by teacher ID: " + teacherId);
        AssignmentListResponse assignmentListResponse;

        try {
            Optional<Teacher> teacher = teacherRepository.findById(teacherId);

            if (teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + teacherId);
                throw new IllegalArgumentException("No teacher found with ID: " + teacherId);
            }

            List<Assignment> assignments = assignmentRepository.findByTeacherId(teacherId);

            assignmentListResponse = AssignmentListResponse.builder()
                    .assignmentList(assignments)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment list response: " + assignmentListResponse);
            return assignmentListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public AssignmentResponse getAssignmentById(Long assignmentId) {
        Utility.printDebugLogs("Get assignment by ID: " + assignmentId);
        AssignmentResponse assignmentResponse;

        try {
            Optional<Assignment> optionalAssignment = assignmentRepository.findById(assignmentId);

            if (optionalAssignment.isEmpty()) {
                Utility.printDebugLogs("No assignment found with ID: " + assignmentId);
                throw new IllegalArgumentException("No assignment found with ID: " + assignmentId);
            }

            Assignment assignment = optionalAssignment.get();

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(assignment.getAssignmentId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .description(assignment.getDescription())
                    .file(assignment.getFile())
                    .dueDate(assignment.getDueDate())
                    .totalMarks(assignment.getTotalMarks())
                    .visibility(assignment.isVisibility())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment response: " + assignmentResponse);
            return assignmentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public AssignmentListResponse getAssignmentsByCourseId(Long courseId) {
        Utility.printDebugLogs("Get assignments by course ID: " + courseId);
        AssignmentListResponse assignmentListResponse;

        try {
            List<Assignment> assignmentList = assignmentRepository.findByCourseId(courseId);

            if (assignmentList.isEmpty()) {
                Utility.printDebugLogs("No assignments found for course ID: " + courseId);
                throw new IllegalArgumentException("No assignments found for course ID: " + courseId);
            }

            assignmentListResponse = AssignmentListResponse.builder()
                    .assignmentList(assignmentList)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment list response: " + assignmentListResponse);
            return assignmentListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }
}