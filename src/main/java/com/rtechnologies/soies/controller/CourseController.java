package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.dto.CourseListResponse;
import com.rtechnologies.soies.model.dto.CourseResponse;
import com.rtechnologies.soies.service.CourseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/courses")
public class CourseController {

        @Autowired
        private CourseService courseService;

        @ApiOperation(value = "Create a new course", response = CourseResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 201, message = "Course created successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @PostMapping
        public ResponseEntity<CourseResponse> createCourse(@RequestBody Course course) {
                CourseResponse courseResponse = courseService.createCourse(course);
                return ResponseEntity.status(courseResponse.getMessageStatus().equals("Success") ? 201 : 500)
                                .body(courseResponse);
        }

        @ApiOperation(value = "Update a course", response = CourseResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Course updated successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 404, message = "Course not found"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @PutMapping
        public ResponseEntity<CourseResponse> updateCourse(@RequestBody Course course) {
                CourseResponse courseResponse = courseService.updateCourse(course);
                return ResponseEntity.status(courseResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(courseResponse);
        }

        @ApiOperation(value = "Delete a course by ID", response = CourseResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Course deleted successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 404, message = "Course not found"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @DeleteMapping("/{courseId}")
        public ResponseEntity<CourseResponse> deleteCourse(@PathVariable Long courseId) {
                CourseResponse courseResponse = courseService.deleteCourse(courseId);
                return ResponseEntity.status(courseResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(courseResponse);
        }

        @ApiOperation(value = "Get all courses", response = CourseListResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Courses fetched successfully"),
                        @ApiResponse(code = 404, message = "No courses found"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<CourseListResponse> getAllCourses() {
                CourseListResponse courseListResponse = courseService.getAllCourses();
                return ResponseEntity.status(courseListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(courseListResponse);
        }

        @ApiOperation(value = "Get all courses by grade", response = CourseListResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Courses fetched successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 404, message = "No courses found"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @GetMapping("/by-grade/{grade}")
        public ResponseEntity<CourseListResponse> getAllCoursesByGrade(@PathVariable String grade) {
                CourseListResponse courseListResponse = courseService.getAllCoursesByGrade(grade);
                return ResponseEntity.status(courseListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(courseListResponse);
        }

        @ApiOperation(value = "Get a course by ID", response = CourseResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Course fetched successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 404, message = "Course not found"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @GetMapping("/{courseId}")
        public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long courseId) {
                CourseResponse courseResponse = courseService.getCourseById(courseId);
                return ResponseEntity.status(courseResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(courseResponse);
        }

        @ApiOperation(value = "Get Courses by Teacher ID")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieved courses", response = CourseListResponse.class),
                        @ApiResponse(code = 400, message = "Bad Request"),
                        @ApiResponse(code = 404, message = "Teacher not found"),
                        @ApiResponse(code = 500, message = "Internal Server Error")
        })
        @GetMapping("/teacher")
        public ResponseEntity<CourseListResponse> getCoursesByTeacherId(@RequestParam("teacherId") Long teacherId) {
                CourseListResponse courseListResponse = courseService.getCoursesByTeacherId(teacherId);
                return ResponseEntity.status(courseListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(courseListResponse);
        }
}
