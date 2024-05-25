package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Quiz;
import com.rtechnologies.soies.model.association.QuizSubmission;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.service.QuizService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @ApiOperation(value = "Create a new quiz", response = QuizResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quiz created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody CreateQuizRequest quiz) {
        QuizResponse response = quizService.createQuiz(quiz);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Submit a quiz as a student", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quiz submitted successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/submit")
    public ResponseEntity<String> submitQuiz(@RequestBody QuizSubmissionRequest quiz) {
        String response = quizService.submitQuiz(quiz);
        return ResponseEntity.status(response.equals("Quiz submitted successfully") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Update an existing quiz", response = QuizResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quiz updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Quiz not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/update")
    public ResponseEntity<QuizResponse> updateQuiz(@RequestBody QuizRequest quiz) {
        QuizResponse response = quizService.updateQuiz(quiz);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Delete a quiz", response = QuizResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quiz deleted successfully"),
            @ApiResponse(code = 404, message = "Quiz not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/delete/{quizId}")
    public ResponseEntity<QuizResponse> deleteQuiz(@PathVariable Long quizId) {
        QuizResponse response = quizService.deleteQuiz(quizId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get a quiz by ID", response = QuizResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quiz retrieved successfully"),
            @ApiResponse(code = 404, message = "Quiz not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get/{quizId}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long quizId) {
        QuizResponse response = quizService.getQuizById(quizId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get a quiz submissions by ID", response = QuizSubmissionListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quiz submissions retrieved successfully"),
            @ApiResponse(code = 404, message = "Quiz not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get/submissions/{quizId}")
    public ResponseEntity<QuizSubmissionListResponse> getQuizSubmissionsById(@PathVariable Long quizId) {
        QuizSubmissionListResponse response = quizService.getAllQuizSubmission(quizId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get quizzes by course ID and student roll number", response = QuizListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quizzes retrieved successfully"),
            @ApiResponse(code = 404, message = "No quizzes found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/getByCourse/{courseId}")
    public ResponseEntity<QuizListResponse> getQuizzesByCourseId(@PathVariable Long courseId) {
        QuizListResponse response = quizService.getQuizzesByCourseId(courseId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }


    @ApiOperation(value = "Get quizzes by course ID and student roll number", response = QuizListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quizzes retrieved successfully"),
            @ApiResponse(code = 404, message = "No quizzes found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/getByCourse/{courseId}/{studentRollNumber}")
    public ResponseEntity<QuizListResponse> getQuizzesByCourseIdAndStuRollNum(@PathVariable Long courseId,
                                                                 @PathVariable String studentRollNumber) {
        QuizListResponse response = quizService.getQuizzesByCourseId(courseId,studentRollNumber);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }
}
