package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.service.AssignmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @ApiOperation(value = "Create a new assignment", response = AssignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<AssignmentResponse> createAssignment(
                                                                @RequestParam("file") MultipartFile file,
                                                                @RequestParam("courseId") long courseId,
                                                                @RequestParam(name = "teacherId", required=false) Long teacherId,
                                                                @RequestParam("assignmentTitle") String assignmentTitle,
                                                                @RequestParam(name = "description", required=false) String description,
                                                                @RequestParam("term") String term,
                                                                @RequestParam("section") String section,
                                                                @RequestParam("totalMarks") int totalMarks,
                                                                @RequestParam("visibility") boolean visibility,
                                                                @RequestParam(name = "dueDate", required=false) String dueDate) {

        System.out.println("In the request: " );
        AssignmentRequest assignmentRequest = AssignmentRequest.builder()
                .courseId(courseId)
                .teacherId(teacherId==null?-1:teacherId)
                .assignmentTitle(assignmentTitle)
                .file(file)
                .description(description)
                .totalMarks(totalMarks)
                .visibility(visibility)
                .term(term)
                .dueDate(dueDate)
                .section(section)
                .build();


        AssignmentResponse response = assignmentService.createAssignment(assignmentRequest);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Submit an assignment", response = AssignmentSubmissionResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/submit")
    public ResponseEntity<AssignmentSubmissionResponse> submitAssignment(
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam("studentId") Long studentId,
            @RequestParam("submittedFile") MultipartFile submittedFile,
            @RequestParam("comments") String comments) {

        AssignmentSubmissionRequest submissionRequest = AssignmentSubmissionRequest.builder()
                .assignmentId(assignmentId)
                .studentId(studentId)
                .submittedFile(submittedFile)
                .comments(comments)
                .obtainedMarks(0)
                .build();

        AssignmentSubmissionResponse response = assignmentService.submitAssignment(submissionRequest);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Update an existing assignment", response = AssignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Assignment not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/update")
    public ResponseEntity<AssignmentResponse> updateAssignment(@ModelAttribute AssignmentRequest assignment) {
        AssignmentResponse response = assignmentService.updateAssignment(assignment);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get all students assignment submissions with pagination", response = AssignmentSubmissionListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Submissions retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-all-assignment-submissions/{assignmentId}")
    public ResponseEntity<AssignmentSubmissionListResponse> getAllAssignmentSubmissions(
            @PathVariable Long assignmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        AssignmentSubmissionListResponse studentListResponse = assignmentService.getAssignmentSubmissions(assignmentId, page, size);
        return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentListResponse);
    }


    @ApiOperation(value = "Get student assignment submission with student Id", response = AssignmentSubmissionListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Submission retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-assignment-submission/{assignmentId}/{studentRollNumber}")
    public ResponseEntity<AssignmentSubmissionResponse> getAssignmentSubmissions(
            @PathVariable Long assignmentId,
            @PathVariable String studentRollNumber
            ) {
        AssignmentSubmissionResponse studentListResponse = assignmentService.getAssignmentSubmissionById(assignmentId, studentRollNumber);
        return ResponseEntity.status(200)
                .body(studentListResponse);
    }

    @ApiOperation(value = "Mark submitted assignment", response = AssignmentSubmissionResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment marked successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Assignment not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/mark-assignment")
    public ResponseEntity<AssignmentSubmissionResponse> markAssignment(@RequestBody MarkAssignmentRequest assignment) {
        AssignmentSubmissionResponse response = assignmentService.markAssignment(assignment);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }
    @ApiOperation(value = "Delete an assignment", response = AssignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment deleted successfully"),
            @ApiResponse(code = 404, message = "Assignment not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/delete/{assignmentId}")
    public ResponseEntity<AssignmentResponse> deleteAssignment(@PathVariable Long assignmentId) {
        AssignmentResponse response = assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get all assignments by teacher ID", response = AssignmentListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignments retrieved successfully"),
            @ApiResponse(code = 404, message = "No assignments found for the given teacher ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/getAllByTeacher/{teacherId}")
    public ResponseEntity<AssignmentListResponse> getAllAssignmentsByTeacherId(@PathVariable Long teacherId) {
        AssignmentListResponse response = assignmentService.getAllAssignmentsByTeacherId(teacherId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get an assignment by ID", response = AssignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment retrieved successfully"),
            @ApiResponse(code = 404, message = "Assignment not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable Long assignmentId) {
        AssignmentResponse response = assignmentService.getAssignmentById(assignmentId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get assignments by course ID", response = AssignmentListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignments retrieved successfully"),
            @ApiResponse(code = 404, message = "No assignments found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/getByCourse/{courseId}/{section}/{studentRollNum}")
    public ResponseEntity<AssignmentListResponse> getAssignmentsByCourseId(@PathVariable Long courseId,
                                                                           @PathVariable String section,
                                                                           @PathVariable String studentRollNum) {
        AssignmentListResponse response = assignmentService.getAssignmentsByCourseId(courseId,section, studentRollNum);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get assignments by course ID and section", response = AssignmentListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignments retrieved successfully"),
            @ApiResponse(code = 404, message = "No assignments found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/getByCourse/{courseId}/{section}")
    public ResponseEntity<AssignmentListResponse> getAssignmentsByCourseId(@PathVariable Long courseId,
                                                                           @PathVariable String section) {
        AssignmentListResponse response = assignmentService.getAssignmentsByCourseId(courseId,section);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }
}
