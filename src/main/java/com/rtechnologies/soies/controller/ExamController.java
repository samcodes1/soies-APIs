package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Exam;
import com.rtechnologies.soies.model.dto.ExamListResponse;
import com.rtechnologies.soies.model.dto.ExamResponse;
import com.rtechnologies.soies.service.ExamService;
import com.rtechnologies.soies.utilities.Utility;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @ApiOperation(value = "Create an exam", response = ExamResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Exam created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@RequestBody Exam exam) {
        Utility.printDebugLogs("Received create exam request: " + exam.toString());
        ExamResponse examResponse = examService.createExam(exam);
        return ResponseEntity.status(HttpStatus.CREATED).body(examResponse);
    }

    @ApiOperation(value = "Update an exam", response = ExamResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Exam updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/{examId}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long examId, @RequestBody Exam exam) {
        Utility.printDebugLogs("Received update exam request for ID " + examId + ": " + exam.toString());
        ExamResponse examResponse = examService.updateExam(exam);
        return ResponseEntity.ok(examResponse);
    }

    @ApiOperation(value = "Delete an exam", response = ExamResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Exam deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{examId}")
    public ResponseEntity<ExamResponse> deleteExam(@PathVariable Long examId) {
        Utility.printDebugLogs("Received delete exam request for ID: " + examId);
        ExamResponse examResponse = examService.deleteExam(examId);
        return ResponseEntity.ok(examResponse);
    }

    @ApiOperation(value = "Get exams by course ID", response = ExamListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Exams retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "No exams found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ExamListResponse> getExamsByCourseId(@PathVariable Long courseId) {
        Utility.printDebugLogs("Received get exams by course ID request: " + courseId);
        ExamListResponse examListResponse = examService.getExamsByCourseId(courseId);
        return ResponseEntity.ok(examListResponse);
    }

    @ApiOperation(value = "Get an exam by ID", response = ExamResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Exam retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "No exam found for the given ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long examId) {
        Utility.printDebugLogs("Received get exam by ID request: " + examId);
        ExamResponse examResponse = examService.getExamById(examId);
        return ResponseEntity.ok(examResponse);
    }
}