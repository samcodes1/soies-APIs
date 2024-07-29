package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.StudentCourse;
import com.rtechnologies.soies.model.dto.StudentResponse;
import com.rtechnologies.soies.service.StudentCourseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-course")
public class StudentCourseController {
    @Autowired
    private StudentCourseService studentCourseService;

    @ApiOperation(value = "Create a new student", response = StudentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Student created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/create-student-course")
    public ResponseEntity<StudentCourse> createStudent() {
        StudentCourse studentResponse = studentCourseService.createStudentCourse();
        return ResponseEntity.status(200)
                .body(studentResponse);
    }

    @GetMapping("/courses/student/{studentId}")
    public ResponseEntity<List<Course>> getCoursesByStudentId(@PathVariable Long studentId) {
        List<Course> courses = studentCourseService.getCoursesByStudentId(studentId);
        return ResponseEntity.ok(courses);
    }
}
