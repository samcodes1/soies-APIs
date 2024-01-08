package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Lecture;
import com.rtechnologies.soies.model.dto.LectureListResponse;
import com.rtechnologies.soies.model.dto.LectureResponse;
import com.rtechnologies.soies.service.LectureService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;

    @ApiOperation(value = "Add a new lecture", response = LectureResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Lecture added successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<LectureResponse> addLecture(@RequestBody Lecture lecture) {
        LectureResponse lectureResponse = lectureService.addLecture(lecture);
        return ResponseEntity.status(lectureResponse.getMessageStatus().equals("Success") ? 201 : 500)
                .body(lectureResponse);
    }

    @ApiOperation(value = "Update a lecture", response = LectureResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lecture updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Lecture not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping
    public ResponseEntity<LectureResponse> updateLecture(@RequestBody Lecture lecture) {
        LectureResponse lectureResponse = lectureService.updateLecture(lecture);
        return ResponseEntity.status(lectureResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(lectureResponse);
    }

    @ApiOperation(value = "Delete a lecture by ID", response = LectureResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lecture deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<LectureResponse> deleteLecture(@PathVariable long lectureId) {
        LectureResponse lectureResponse = lectureService.deleteLecture(lectureId);
        return ResponseEntity.status(lectureResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(lectureResponse);
    }

    @ApiOperation(value = "Get all lectures by course ID", response = LectureListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lectures fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "No lectures found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<LectureListResponse> getLecturesByCourseId(@PathVariable long courseId) {
        LectureListResponse lectureListResponse = lectureService.getLecturesByCourseId(courseId);
        return ResponseEntity.status(lectureListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(lectureListResponse);
    }

    @ApiOperation(value = "Get lecture by ID", response = LectureResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lecture fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Lecture not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureResponse> getLectureById(@PathVariable long lectureId) {
        LectureResponse lectureResponse = lectureService.getLectureById(lectureId);
        return ResponseEntity.status(lectureResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(lectureResponse);
    }

    @ApiOperation(value = "Set visibility for a lecture", response = LectureResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Visibility set successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Lecture not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping("/{lectureId}/set-visibility")
    public ResponseEntity<LectureResponse> setVisibility(@PathVariable long lectureId, @RequestParam boolean isVisible) {
        LectureResponse lectureResponse = lectureService.setVisibility(lectureId, isVisible);
        return ResponseEntity.status(lectureResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(lectureResponse);
    }
}
