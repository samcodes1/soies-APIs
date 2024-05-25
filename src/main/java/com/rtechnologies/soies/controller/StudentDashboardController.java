package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.dto.StudentDashboardResponse;
import com.rtechnologies.soies.model.dto.StudentListResponse;
import com.rtechnologies.soies.service.StudentDashboardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-dashboard")
public class StudentDashboardController {

    @Autowired
    private StudentDashboardService studentDashboardService;

    @ApiOperation(value = "Get Student Dashboard", response = StudentDashboardResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved student dashboard"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Student not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("/get")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(@RequestParam String rollNumber) {
        StudentDashboardResponse studentListResponse = studentDashboardService.getStudentDashboard(rollNumber);
        return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentListResponse);
    }

    // Other methods (getStudentName, getAttendancePer, getTermCompletion) remain the same
}
