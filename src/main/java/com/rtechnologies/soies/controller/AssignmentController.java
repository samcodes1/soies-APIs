package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.dto.AssignmentListResponse;
import com.rtechnologies.soies.model.dto.AssignmentRequest;
import com.rtechnologies.soies.model.dto.AssignmentResponse;
import com.rtechnologies.soies.model.dto.TeacherResponse;
import com.rtechnologies.soies.service.AssignmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AssignmentResponse> createAssignment(@RequestBody AssignmentRequest assignment) {
        AssignmentResponse response = assignmentService.createAssignment(assignment);
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
    public ResponseEntity<AssignmentResponse> updateAssignment(@RequestBody AssignmentRequest assignment) {
        AssignmentResponse response = assignmentService.updateAssignment(assignment);
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
    @GetMapping("/getByCourse/{courseId}")
    public ResponseEntity<AssignmentListResponse> getAssignmentsByCourseId(@PathVariable Long courseId) {
        AssignmentListResponse response = assignmentService.getAssignmentsByCourseId(courseId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }
}
