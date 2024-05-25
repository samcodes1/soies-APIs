package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.dto.TeacherCourseListResponse;
import com.rtechnologies.soies.model.dto.TeacherCourseResponse;
import com.rtechnologies.soies.service.TeacherCourseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/teacher-courses")
public class TeacherCourseController {

    @Autowired
    private TeacherCourseService teacherCourseService;

    @ApiOperation(value = "Add a new teacher course", response = TeacherCourseResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Teacher course added successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher or course not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<TeacherCourseResponse> addTeacherCourse(@RequestBody TeacherCourse teacherCourse) {
        TeacherCourseResponse teacherCourseResponse = teacherCourseService.addTeacherCourse(teacherCourse);
        return ResponseEntity.status(teacherCourseResponse.getMessageStatus().equals("Success") ? 201 : 500)
                .body(teacherCourseResponse);
    }

    @ApiOperation(value = "Update a teacher course", response = TeacherCourseResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher course updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher or course not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping
    public ResponseEntity<TeacherCourseResponse> updateTeacherCourse(@RequestBody TeacherCourse teacherCourse) {
        TeacherCourseResponse teacherCourseResponse = teacherCourseService.updateTeacherCourse(teacherCourse);
        return ResponseEntity.status(teacherCourseResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherCourseResponse);
    }

    @ApiOperation(value = "Delete a teacher course by ID", response = TeacherCourseResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher course deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{teacherCourseId}")
    public ResponseEntity<TeacherCourseResponse> deleteTeacherCourse(@PathVariable Long teacherCourseId) {
        TeacherCourseResponse teacherCourseResponse = teacherCourseService.deleteTeacherCourse(teacherCourseId);
        return ResponseEntity.status(teacherCourseResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherCourseResponse);
    }

    @ApiOperation(value = "Get all courses by teacher ID", response = TeacherCourseListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Courses fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "No courses found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<TeacherCourseListResponse> getAllCoursesByTeacherId(@PathVariable Long teacherId) {
        TeacherCourseListResponse teacherCourseListResponse = teacherCourseService.getAllCoursesByTeacherId(teacherId);
        return ResponseEntity.status(teacherCourseListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherCourseListResponse);
    }
}
