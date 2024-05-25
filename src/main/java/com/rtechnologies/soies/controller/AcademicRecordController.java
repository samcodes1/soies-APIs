package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.dto.AcademicRecordResponse;
import com.rtechnologies.soies.model.dto.AssignmentResponse;
import com.rtechnologies.soies.service.AcademicRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AcademicRecordController {
    @Autowired
    private AcademicRecordService academicRecordService;

    @ApiOperation(value = "Get Academic Record",
            notes = "Get academic record details for a student based on student roll number, term, and academic category",
            response = AcademicRecordResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved academic record", response = AcademicRecordResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Student not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("/academic-record")
    public ResponseEntity<AcademicRecordResponse> getAcademicRecord(
            @RequestParam String studentRollNumber,
            @RequestParam String term,
            @RequestParam String academicCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        AcademicRecordResponse response = academicRecordService.getAcademicRecord(studentRollNumber,
                term,
                academicCategory,
                page,
                size);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }
}
