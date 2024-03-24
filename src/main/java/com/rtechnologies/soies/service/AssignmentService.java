package com.rtechnologies.soies.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.association.AssignmentSubmission;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.*;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    public AssignmentResponse createAssignment(AssignmentRequest assignment) {
        Utility.printDebugLogs("Assignment creation request: " + assignment.toString());
        AssignmentResponse assignmentResponse;

        try {
            if (assignment == null) {
                Utility.printDebugLogs("Assignment creation request is null");
                throw new NotFoundException("Corrupt data received");
            }

            //Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(assignment.getTeacherId());
            if(teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + assignment.getTeacherId());
                throw new NotFoundException("No teacher found with ID: " + assignment.getTeacherId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(assignment.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + assignment.getCourseId());
                throw new NotFoundException("No course found with ID: " + assignment.getCourseId());
            }

            String fileName = "";
            fileName = assignment.getAssignmentTitle().toLowerCase() + "-" + assignment.getCourseId();
            Assignment createdAssignment = new Assignment();
            try {
                String folder = "uploaded-assignments";
                String publicId = folder + "/" + fileName;
                Map data = cloudinary.uploader().upload(assignment.getFile().getBytes(), ObjectUtils.asMap("public_id", publicId));
                String url =  data.get("url").toString();

                System.out.println("URL is: " + url);
                Assignment finalAssignment = new Assignment();
                finalAssignment.setAssignmentTitle(assignment.getAssignmentTitle());
                finalAssignment.setFile(url);
                finalAssignment.setDescription(assignment.getDescription());
                finalAssignment.setVisibility(true);
                finalAssignment.setCourseId(assignment.getCourseId());
                finalAssignment.setTeacherId(assignment.getTeacherId());
                finalAssignment.setTotalMarks(assignment.getTotalMarks());
                finalAssignment.setTerm(assignment.getTerm());
                finalAssignment.setSection(assignment.getSection());
                createdAssignment = assignmentRepository.save(finalAssignment);
            } catch (IOException ioException) {
                throw new RuntimeException("File uploading failed");
            }

            Utility.printDebugLogs("Assignment created successfully: " + createdAssignment);

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(createdAssignment.getAssignmentId())
                    .course(course.get())
                    .teacher(teacher.get())
                    .assignmentTitle(createdAssignment.getAssignmentTitle())
                    .description(createdAssignment.getDescription())
                    .file(createdAssignment.getFile())
                    .totalMarks(createdAssignment.getTotalMarks())
                    .visibility(createdAssignment.isVisibility())
                    .term(createdAssignment.getTerm())
                    .section(createdAssignment.getSection())
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

    public AssignmentResponse updateAssignment(AssignmentRequest assignment) {
        Utility.printDebugLogs("Assignment update request: " + assignment.toString());
        AssignmentResponse assignmentResponse;

        try {
            if (assignment == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            //Check for assignment
            Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignment.getAssignmentId());
            if(assignmentOptional.isEmpty()){
                throw new NotFoundException("No Assignment found with ID: " + assignment.getAssignmentId());
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(assignment.getTeacherId());
            if (teacher.isEmpty()) {
                throw new NotFoundException("No teacher found with ID: " + assignment.getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(assignment.getCourseId());
            if (course.isEmpty()) {
                throw new NotFoundException("No course found with ID: " + assignment.getCourseId());
            }


            String fileName = "";
            fileName = assignment.getAssignmentTitle().toLowerCase() + "-" + assignment.getCourseId();
            Assignment updatedAssignment = new Assignment();
            try {
                String folder = "uploaded-assignments";
                String publicId = folder + "/" + fileName;
                Map data = cloudinary.uploader().upload(assignment.getFile().getBytes(), ObjectUtils.asMap("public_id", publicId));
                String url =  data.get("url").toString();

                Assignment finalAssignment = new Assignment();
                finalAssignment.setAssignmentId(assignment.getAssignmentId());
                finalAssignment.setAssignmentTitle(assignment.getAssignmentTitle());
                finalAssignment.setFile(url);
                finalAssignment.setDescription(assignment.getDescription());
                finalAssignment.setVisibility(true);
                finalAssignment.setCourseId(assignment.getCourseId());
                finalAssignment.setTeacherId(assignment.getTeacherId());
                finalAssignment.setTotalMarks(assignment.getTotalMarks());
                finalAssignment.setTerm(assignment.getTerm());
                finalAssignment.setSection(assignment.getSection());
                updatedAssignment = assignmentRepository.save(finalAssignment);
            } catch (IOException ioException) {
                throw new RuntimeException("File uploading failed");
            }

            Utility.printDebugLogs("Assignment updated successfully: " + updatedAssignment);

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(updatedAssignment.getAssignmentId())
                    .course(course.get())
                    .teacher(teacher.get())
                    .assignmentTitle(updatedAssignment.getAssignmentTitle())
                    .description(updatedAssignment.getDescription())
                    .file(updatedAssignment.getFile())
                    .totalMarks(updatedAssignment.getTotalMarks())
                    .visibility(updatedAssignment.isVisibility())
                    .term(updatedAssignment.getTerm())
                    .section(updatedAssignment.getSection())
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
                throw new NotFoundException("No assignment found with ID: " + assignmentId);
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
                throw new NotFoundException("No teacher found with ID: " + teacherId);
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
                throw new NotFoundException("No assignment found with ID: " + assignmentId);
            }

            Assignment assignment = optionalAssignment.get();

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(assignment.getAssignmentId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .description(assignment.getDescription())
                    .file(assignment.getFile())
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

    public AssignmentListResponse getAssignmentsByCourseId(Long courseId, String section, String studentRollNum) {
        Utility.printDebugLogs("Get assignments by course ID: " + courseId);
        AssignmentListResponse assignmentListResponse;

        try {
            List<Assignment> assignmentList = assignmentRepository.findByCourseIdAndSection(courseId, section);
            List<Assignment> finalList = new ArrayList<>();
            if (assignmentList.isEmpty()) {
                Utility.printDebugLogs("No assignments found for course ID: " + courseId);
                throw new NotFoundException("No assignments found for course ID: " + courseId);
            }

            List<AssignmentSubmission> assignmentSubmissionList =
                    assignmentSubmissionRepository.findByStudentRollNumber(studentRollNum);
           finalList = assignmentList;
            if(!assignmentSubmissionList.isEmpty()) {
                for(int i =0; i<assignmentSubmissionList.size(); i++){
                    for(Assignment assignment : assignmentList){
                        if(Objects.equals(assignment.getAssignmentId(), assignmentSubmissionList.get(i).getAssignmentId())) {
                            finalList.remove(assignment);
                            break;
                        }
                    }

                }

                assignmentListResponse = AssignmentListResponse.builder()
                        .assignmentList(finalList)
                        .messageStatus("Success")
                        .build();
            } else {
                assignmentListResponse = AssignmentListResponse.builder()
                        .assignmentList(assignmentList)
                        .messageStatus("Success")
                        .build();
            }


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

    public AssignmentListResponse getAssignmentsByCourseId(Long courseId, String section) {
        Utility.printDebugLogs("Get assignments by course ID: " + courseId);
        AssignmentListResponse assignmentListResponse;

        try {
            List<Assignment> assignmentList = assignmentRepository.findByCourseIdAndSection(courseId, section);
            if (assignmentList.isEmpty()) {
                Utility.printDebugLogs("No assignments found for course ID: " + courseId);
                throw new NotFoundException("No assignments found for course ID: " + courseId);
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

    public AssignmentSubmissionResponse submitAssignment(AssignmentSubmissionRequest assignment){
        Utility.printDebugLogs("Assignment submission request: " + assignment.toString());
        AssignmentSubmissionResponse assignmentResponse;

        try {
            if (assignment == null) {
                Utility.printDebugLogs("Assignment submission request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }


            //Check for assignment ID
            Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignment.getAssignmentId());
            if(assignmentOptional.isEmpty()) {
                Utility.printDebugLogs("No assignment found with ID: " + assignment.getAssignmentId());
                throw new NotFoundException("No assignment found with ID: " + assignment.getAssignmentId());
            }

            //Check for student
            Optional<Student> studentOptional = studentRepository.findById(assignment.getStudentId());
            if(studentOptional.isEmpty()) {
                Utility.printDebugLogs("No student found with ID: " + assignment.getStudentId());
                throw new NotFoundException("No student found with ID: " + assignment.getStudentId());
            }

            Optional<AssignmentSubmission> assignmentSubmission = assignmentSubmissionRepository.findByAssignmentIdAndStudentRollNumber(
                    assignment.getAssignmentId(), studentOptional.get().getRollNumber());

            long submissionId = 0;
            if(assignmentSubmission.isPresent()) {
                submissionId = assignmentSubmission.get().getSubmissionId();
            }

            String fileName = "";
            fileName = assignment.getAssignmentId() + "-" + assignment.getStudentId();
            AssignmentSubmission finalAssignment = new AssignmentSubmission();
            try {
                String folder = "submitted-assignments";
                String publicId = folder + "/" + fileName;
                Map data = cloudinary.uploader().upload(assignment.getSubmittedFile().getBytes(), ObjectUtils.asMap("public_id", publicId));
                String url =  data.get("url").toString();

                System.out.println("URL is: " + url);
                if(submissionId != 0){
                    finalAssignment.setSubmissionId(submissionId);
                }

                finalAssignment.setAssignmentId(assignment.getAssignmentId());
                finalAssignment.setStudentRollNumber(Long.toString(assignment.getStudentId()));
                finalAssignment.setSubmissionDate(assignment.getSubmissionDate());
                finalAssignment.setSubmittedFileURL(url);
                finalAssignment.setStudentName(studentOptional.get().getStudentName());
                finalAssignment.setComments("pending");
                finalAssignment.setObtainedMarks(-1);
                finalAssignment.setObtainedGrade("pending");
                finalAssignment = assignmentSubmissionRepository.save(finalAssignment);
            } catch (IOException ioException) {
                throw new RuntimeException("File uploading failed");
            }

            Utility.printDebugLogs("Assignment created successfully: " + finalAssignment);

            assignmentResponse = AssignmentSubmissionResponse.builder()
                    .submissionId(finalAssignment.getSubmissionId())
                    .assignmentId(finalAssignment.getAssignmentId())
                    .studentId(finalAssignment.getStudentRollNumber())
                    .submissionDate(finalAssignment.getSubmissionDate())
                    .submittedFileURL(finalAssignment.getSubmittedFileURL())
                    .comments("pending")
                    .obtainedMarks(-1)
                    .grade("pending")
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment submission response: " + assignmentResponse);
            return assignmentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public AssignmentSubmissionListResponse getAssignmentSubmissions(Long assignmentId, int page, int size) {
        Utility.printDebugLogs("Get assignment submissions by assignment ID: " + assignmentId);
        AssignmentSubmissionListResponse assignmentSubmissionListResponse;

        try {
            Page<AssignmentSubmission> assignmentSubmissionsPage = assignmentSubmissionRepository.findByAssignmentId(assignmentId, PageRequest.of(page, size));
            List<AssignmentSubmission> assignmentList = assignmentSubmissionsPage.getContent();

            assignmentSubmissionListResponse = AssignmentSubmissionListResponse.builder()
                    .assignmentSubmissionResponseList(assignmentList)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment list response: " + assignmentSubmissionListResponse);
            return assignmentSubmissionListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }


    public AssignmentSubmissionResponse getAssignmentSubmissionById(Long assignmentId, String studentRollNumber) {
        AssignmentSubmissionResponse assignmentSubmissionListResponse;

        try {
            Optional<AssignmentSubmission> assignmentSubmissionsPage = assignmentSubmissionRepository.findByAssignmentIdAndStudentRollNumber(assignmentId, studentRollNumber);

            if(assignmentSubmissionsPage.isEmpty()){
                throw new NotFoundException("No submission found");
            }
            assignmentSubmissionListResponse = AssignmentSubmissionResponse.builder()
                    .submissionId(assignmentSubmissionsPage.get().getSubmissionId())
                    .assignmentId(assignmentSubmissionsPage.get().getAssignmentId())
                    .studentId(assignmentSubmissionsPage.get().getStudentRollNumber())
                    .submittedFileURL(assignmentSubmissionsPage.get().getSubmittedFileURL())
                    .comments(assignmentSubmissionsPage.get().getComments())
                    .obtainedMarks(assignmentSubmissionsPage.get().getObtainedMarks())
                    .grade(assignmentSubmissionsPage.get().getObtainedGrade())
                    .submissionDate(assignmentSubmissionsPage.get().getSubmissionDate())
                    .studentName(assignmentSubmissionsPage.get().getStudentName())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment list response: " + assignmentSubmissionListResponse);
            return assignmentSubmissionListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    private static final Map<String, String> GRADE_MAPPING;

    static {
        GRADE_MAPPING = new HashMap<>();
        GRADE_MAPPING.put("A", "90-100");
        GRADE_MAPPING.put("B", "80-89");
        GRADE_MAPPING.put("C", "70-79");
        GRADE_MAPPING.put("D", "60-69");
        GRADE_MAPPING.put("F", "0-59");
    }

    public AssignmentSubmissionResponse markAssignment(MarkAssignmentRequest markAssignmentRequest){
        Utility.printDebugLogs("Mark Assignment request " + markAssignmentRequest);
        AssignmentSubmissionResponse assignmentResponse;

        try {
            if (markAssignmentRequest == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            //Check for assignment ID
            Optional<AssignmentSubmission> assignmentOptional = assignmentSubmissionRepository.findById(markAssignmentRequest.getSubmissionId());

            if(assignmentOptional.isEmpty()) {
                throw new NotFoundException("No assignment submission with ID: " + markAssignmentRequest.getSubmissionId());
            }

            assignmentOptional.get().setComments(markAssignmentRequest.getFeedback());
            assignmentOptional.get().setObtainedMarks(markAssignmentRequest.getMarks());
            for (Map.Entry<String, String> entry : GRADE_MAPPING.entrySet()) {
                String[] range = entry.getValue().split("-");
                int lowerBound = Integer.parseInt(range[0]);
                int upperBound = Integer.parseInt(range[1]);

                if (markAssignmentRequest.getMarks() >= lowerBound && markAssignmentRequest.getMarks() <= upperBound) {
                    assignmentOptional.get().setObtainedGrade(entry.getKey());
                }
            }

            AssignmentSubmission finalAssignment = assignmentSubmissionRepository.save(assignmentOptional.get());
            Utility.printDebugLogs("Assignment created successfully: " + finalAssignment);

            assignmentResponse = AssignmentSubmissionResponse.builder()
                    .submissionId(finalAssignment.getSubmissionId())
                    .assignmentId(finalAssignment.getAssignmentId())
                    .studentId(finalAssignment.getStudentRollNumber())
                    .submissionDate(finalAssignment.getSubmissionDate())
                    .submittedFileURL(finalAssignment.getSubmittedFileURL())
                    .comments(finalAssignment.getComments())
                    .obtainedMarks(finalAssignment.getObtainedMarks())
                    .grade(finalAssignment.getObtainedGrade())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment submission response: " + assignmentResponse);
            return assignmentResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

}
