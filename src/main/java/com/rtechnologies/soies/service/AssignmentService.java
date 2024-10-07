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
import java.util.ArrayList;

import javax.transaction.Transactional;

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

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest assignment) {
        Utility.printDebugLogs("Assignment creation request: " + assignment.toString());
        AssignmentResponse assignmentResponse;

        if (assignment == null) {
            Utility.printDebugLogs("Assignment creation request is null");
            throw new NotFoundException("Corrupt data received");
        }

        // Check for course
        Optional<Course> course = courseRepository.findById(assignment.getCourseId());
        if (course.isEmpty()) {
            Utility.printDebugLogs("No course found with ID: " + assignment.getCourseId());
            throw new NotFoundException("No course found with ID: " + assignment.getCourseId());
        }

        String fileName = assignment.getAssignmentTitle().toLowerCase() + "-" + assignment.getCourseId();
        Assignment createdAssignment = new Assignment();

        try {
            String folder = "uploaded-assignments";
            String publicId = folder + "/" + fileName;

            // Upload file to Cloudinary
            Map<String, Object> data = cloudinary.uploader().upload(assignment.getFile().getBytes(),
                    ObjectUtils.asMap("public_id", publicId));

            // Retrieve the secure URL (HTTPS)
            String secureUrl = data.get("secure_url").toString();

            System.out.println("Secure URL is: " + secureUrl);
            Assignment finalAssignment = new Assignment();
            finalAssignment.setAssignmentTitle(assignment.getAssignmentTitle());
            finalAssignment.setFile(secureUrl);
            finalAssignment.setDescription(assignment.getDescription());
            finalAssignment.setVisibility(true);
            finalAssignment.setCourseId(assignment.getCourseId());
            finalAssignment.setTeacherId(assignment.getTeacherId()); // Set the teacherId directly
            finalAssignment.setTotalMarks(assignment.getTotalMarks());
            finalAssignment.setTerm(assignment.getTerm());
            finalAssignment.setDueDate(assignment.getDueDate());
            finalAssignment.setSection(assignment.getSection());
            createdAssignment = assignmentRepository.save(finalAssignment);
        } catch (IOException ioException) {
            throw new RuntimeException("File uploading failed");
        }

        Utility.printDebugLogs("Assignment created successfully: " + createdAssignment);

        assignmentResponse = AssignmentResponse.builder()
                .assignmentId(createdAssignment.getAssignmentId())
                .course(course.get())
                .teacher(assignment.getTeacherId() != null
                        ? teacherRepository.findById(assignment.getTeacherId()).orElse(null)
                        : null) // Handle teacher retrieval if teacherId is not null
                .assignmentTitle(createdAssignment.getAssignmentTitle())
                .description(createdAssignment.getDescription())
                .file(createdAssignment.getFile())
                .totalMarks(createdAssignment.getTotalMarks())
                .visibility(createdAssignment.isVisibility())
                .term(createdAssignment.getTerm())
                .section(createdAssignment.getSection())
                .dueDate(createdAssignment.getDueDate())
                .messageStatus("Success")
                .build();

        Utility.printDebugLogs("Assignment response: " + assignmentResponse);
        return assignmentResponse;
    }

    @Transactional
    public AssignmentResponse updateAssignment(AssignmentRequest assignment) {
        Utility.printDebugLogs("Assignment update request: " + assignment.toString());
        AssignmentResponse assignmentResponse;

        try {
            if (assignment == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for assignment
            Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignment.getAssignmentId());
            if (assignmentOptional.isEmpty()) {
                throw new NotFoundException("No Assignment found with ID: " + assignment.getAssignmentId());
            }

            Assignment existingAssignment = assignmentOptional.get();

            // Check for teacher
            Teacher teacher = null;
            if (assignment.getTeacherId() != null) {
                Optional<Teacher> teacherOptional = teacherRepository.findById(assignment.getTeacherId());
                if (teacherOptional.isPresent()) {
                    teacher = teacherOptional.get();
                } else {
                    throw new NotFoundException("No teacher found with ID: " + assignment.getTeacherId());
                }
            }

            // Check for course
            Optional<Course> courseOptional = courseRepository.findById(assignment.getCourseId());
            if (courseOptional.isEmpty()) {
                throw new NotFoundException("No course found with ID: " + assignment.getCourseId());
            }

            String fileName = assignment.getAssignmentTitle() != null
                    ? assignment.getAssignmentTitle().toLowerCase() + "-" + assignment.getCourseId()
                    : "assignment-" + assignment.getCourseId();
            String fileUrl = existingAssignment.getFile();

            try {
                if (assignment.getFile() != null && !assignment.getFile().isEmpty()) {
                    String folder = "uploaded-assignments";
                    String publicId = folder + "/" + fileName;
                    Map<?, ?> data = cloudinary.uploader().upload(assignment.getFile().getBytes(),
                            ObjectUtils.asMap("public_id", publicId));
                    fileUrl = data.get("url").toString();
                }
            } catch (IOException ioException) {
                throw new RuntimeException("File uploading failed");
            }

            // Update only the provided fields
            if (assignment.getAssignmentTitle() != null) {
                existingAssignment.setAssignmentTitle(assignment.getAssignmentTitle());
            }
            if (assignment.getDescription() != null) {
                existingAssignment.setDescription(assignment.getDescription());
            }
            if (assignment.getDueDate() != null) {
                existingAssignment.setDueDate(assignment.getDueDate());
            }
            if (assignment.getTotalMarks() != 0) { // Assuming 0 is not a valid update value
                existingAssignment.setTotalMarks(assignment.getTotalMarks());
            }
            if (assignment.getTerm() != null) {
                existingAssignment.setTerm(assignment.getTerm());
            }
            if (assignment.getSection() != null) {
                existingAssignment.setSection(assignment.getSection());
            }
            existingAssignment.setVisibility(assignment.isVisibility());
            existingAssignment.setFile(fileUrl);
            existingAssignment.setCourseId(assignment.getCourseId());
            if (assignment.getTeacherId() != null) {
                existingAssignment.setTeacherId(assignment.getTeacherId());
            }

            Assignment updatedAssignment = assignmentRepository.save(existingAssignment);

            Utility.printDebugLogs("Assignment updated successfully: " + updatedAssignment);

            assignmentResponse = AssignmentResponse.builder()
                    .assignmentId(updatedAssignment.getAssignmentId())
                    .course(courseOptional.get())
                    .teacher(teacher)
                    .assignmentTitle(updatedAssignment.getAssignmentTitle())
                    .description(updatedAssignment.getDescription())
                    .file(fileUrl) // Provide the file URL
                    .dueDate(updatedAssignment.getDueDate())
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
                    .dueDate(assignment.getDueDate())
                    .term(assignment.getTerm())
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
                // Return an empty list with a success messageStatus
                assignmentListResponse = AssignmentListResponse.builder()
                        .assignmentList(new ArrayList<>()) // Empty list
                        .messageStatus("Success") // Success status
                        .build();
            } else {
                List<AssignmentSubmission> assignmentSubmissionList = assignmentSubmissionRepository
                        .findByStudentRollNumber(studentRollNum);

                finalList.addAll(assignmentList);

                if (!assignmentSubmissionList.isEmpty()) {
                    for (AssignmentSubmission submission : assignmentSubmissionList) {
                        finalList.removeIf(assignment -> Objects.equals(assignment.getAssignmentId(),
                                submission.getAssignmentId()));
                    }
                }

                assignmentListResponse = AssignmentListResponse.builder()
                        .assignmentList(finalList)
                        .messageStatus("Success") // Success status
                        .build();
            }

            Utility.printDebugLogs("Assignment list response: " + assignmentListResponse);
            return assignmentListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentListResponse.builder()
                    .assignmentList(new ArrayList<>()) // Empty list in case of error
                    .messageStatus("Invalid input: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentListResponse.builder()
                    .assignmentList(new ArrayList<>()) // Empty list in case of error
                    .messageStatus("Failure: " + e.getMessage())
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
                return AssignmentListResponse.builder()
                        .assignmentList(new ArrayList<>())
                        .messageStatus("Success")
                        .build();
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

    public AssignmentSubmissionResponse submitAssignment(AssignmentSubmissionRequest assignment) {
        Utility.printDebugLogs("Assignment submission request: " + assignment.toString());
        AssignmentSubmissionResponse assignmentResponse;

        try {
            if (assignment == null) {
                Utility.printDebugLogs("Assignment submission request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for assignment ID
            Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignment.getAssignmentId());
            if (assignmentOptional.isEmpty()) {
                Utility.printDebugLogs("No assignment found with ID: " + assignment.getAssignmentId());
                throw new NotFoundException("No assignment found with ID: " + assignment.getAssignmentId());
            }

            // Check for student
            Optional<Student> studentOptional = studentRepository.findById(assignment.getStudentId());
            if (studentOptional.isEmpty()) {
                Utility.printDebugLogs("No student found with ID: " + assignment.getStudentId());
                throw new NotFoundException("No student found with ID: " + assignment.getStudentId());
            }

            List<AssignmentSubmission> assignmentSubmission = assignmentSubmissionRepository
                    .findByAssignmentIdAndStudentRollNumber(
                            assignment.getAssignmentId(), studentOptional.get().getRollNumber());

            long submissionId = 0;
            if (!assignmentSubmission.isEmpty()) {
                submissionId = assignmentSubmission.get(assignmentSubmission.size() - 1).getSubmissionId();
            }

            String fileName = "";
            fileName = assignment.getAssignmentId() + "-" + assignment.getStudentId();
            AssignmentSubmission finalAssignment = new AssignmentSubmission();
            try {
                String folder = "submitted-assignments";
                String publicId = folder + "/" + fileName;
                Map data = cloudinary.uploader().upload(assignment.getSubmittedFile().getBytes(),
                        ObjectUtils.asMap("public_id", publicId));
                String url = data.get("url").toString();

                if (submissionId != 0) {
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
                finalAssignment.setTerm(assignmentOptional.get().getTerm());
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
            // Retrieve assignment details
            Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
            if (assignmentOptional.isEmpty()) {
                Utility.printDebugLogs("No assignment found with ID: " + assignmentId);
                return AssignmentSubmissionListResponse.builder()
                        .assignmentSubmissionResponsePage(Page.empty()) // Return an empty page
                        .messageStatus("No submissions found for the given assignment")
                        .build();
            }

            // Retrieve submissions
            Page<AssignmentSubmission> assignmentSubmissionsPage = assignmentSubmissionRepository
                    .findByAssignmentId(assignmentId, PageRequest.of(page, size));

            if (assignmentSubmissionsPage.isEmpty()) {
                Utility.printDebugLogs("No submissions found for assignment ID: " + assignmentId);
                return AssignmentSubmissionListResponse.builder()
                        .assignmentSubmissionResponsePage(Page.empty()) // Return an empty page
                        .messageStatus("No submissions found")
                        .build();
            }

            // Update each AssignmentSubmission with totalMarks, courseId, and dueDate
            assignmentSubmissionsPage.forEach(submission -> {
                submission.setTotalMarks(assignmentOptional.get().getTotalMarks());
                submission.setCourseId(assignmentOptional.get().getCourseId());
                submission.setDueDate(assignmentOptional.get().getDueDate());
                submission.setTerm(assignmentOptional.get().getTerm());
            });

            // Build response
            assignmentSubmissionListResponse = AssignmentSubmissionListResponse.builder()
                    .assignmentSubmissionResponsePage(assignmentSubmissionsPage)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Assignment list response: " + assignmentSubmissionListResponse);
            return assignmentSubmissionListResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionListResponse.builder()
                    .messageStatus(e.toString())
                    .assignmentSubmissionResponsePage(Page.empty()) // Return an empty page on error
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return AssignmentSubmissionListResponse.builder()
                    .messageStatus("Failure")
                    .assignmentSubmissionResponsePage(Page.empty()) // Return an empty page on error
                    .build();
        }
    }


    public AssignmentSubmissionResponse getAssignmentSubmissionById(Long assignmentId, String studentRollNumber) {
        AssignmentSubmissionResponse assignmentSubmissionListResponse;

        try {
            List<AssignmentSubmission> assignmentSubmissionsPage = assignmentSubmissionRepository
                    .findByAssignmentIdAndStudentRollNumber(assignmentId, studentRollNumber);
            int size = assignmentSubmissionsPage.size();
            if (assignmentSubmissionsPage.isEmpty()) {
                throw new NotFoundException("No submission found");
            }
            assignmentSubmissionListResponse = AssignmentSubmissionResponse.builder()
                    .submissionId(assignmentSubmissionsPage.get(size - 1).getSubmissionId())
                    .assignmentId(assignmentSubmissionsPage.get(size - 1).getAssignmentId())
                    .studentId(assignmentSubmissionsPage.get(size - 1).getStudentRollNumber())
                    .submittedFileURL(assignmentSubmissionsPage.get(size - 1).getSubmittedFileURL())
                    .comments(assignmentSubmissionsPage.get(size - 1).getComments())
                    .obtainedMarks(assignmentSubmissionsPage.get(size - 1).getObtainedMarks())
                    .grade(assignmentSubmissionsPage.get(size - 1).getObtainedGrade())
                    .submissionDate(assignmentSubmissionsPage.get(size - 1).getSubmissionDate())
                    .studentName(assignmentSubmissionsPage.get(size - 1).getStudentName())
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

    public AssignmentSubmissionResponse markAssignment(MarkAssignmentRequest markAssignmentRequest) {
        Utility.printDebugLogs("Mark Assignment request " + markAssignmentRequest);
        AssignmentSubmissionResponse assignmentResponse;

        try {
            if (markAssignmentRequest == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for assignment ID
            Optional<AssignmentSubmission> assignmentOptional = assignmentSubmissionRepository
                    .findById(markAssignmentRequest.getSubmissionId());

            if (assignmentOptional.isEmpty()) {
                throw new NotFoundException(
                        "No assignment submission with ID: " + markAssignmentRequest.getSubmissionId());
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

    // new api for getting assignments by courseId and grade
    public AssignmentListResponse getAssignmentsByCourseAndGrade(Long courseId, String grade) {

        Utility.printDebugLogs("Get assignments by course ID: " + courseId + " grade : " + grade);
        AssignmentListResponse assignmentListResponse;
        try {
            List<Assignment> assignmentList = assignmentRepository.findAssignmentsByCourseIdAndGrade(courseId, grade);
            if (assignmentList.isEmpty()) {
                Utility.printDebugLogs("No assignments found for course ID: " + courseId + " and grade : " + grade);
                return AssignmentListResponse.builder()
                        .assignmentList(new ArrayList<>())
                        .messageStatus("Success")
                        .build();
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
