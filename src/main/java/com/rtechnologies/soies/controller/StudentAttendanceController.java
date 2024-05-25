package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.dto.MarkAttendanceResponse;
import com.rtechnologies.soies.service.StudentAttendanceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-attendance")
public class StudentAttendanceController {

    @Autowired
    private StudentAttendanceService attendanceService;

    @ApiOperation(value = "Mark attendance on logout")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Attendance marked successfully"),
            @ApiResponse(code = 400, message = "Bad request") })
    @PostMapping("/mark-attendance")
    public ResponseEntity<MarkAttendanceResponse> markAttendanceOnLogout(@RequestParam Long sessionId) {
        MarkAttendanceResponse response = attendanceService.markAttendanceOnLogout(sessionId);
        HttpStatus status = response.getMessageStatus().equals("Success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }
}
