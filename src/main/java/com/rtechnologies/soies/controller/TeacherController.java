package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.service.TeacherService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/v1/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation(value = "Create a new teacher", response = TeacherResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Teacher created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(produces = "application/json", path = "/create-teacher")
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody CreateTeacherDTO teacher) {
        System.out.println("Done2");
        TeacherResponse teacherResponse = teacherService.createTeacher(teacher);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 201 : 500)
                .body(teacherResponse);
    }

    @PostMapping(produces = "application/json", path = "/hello")
    public Map<String, String> createTeacher() {
        System.out.println("Done2");
        return new HashMap<String, String>();
    }

    @ApiOperation(value = "Delete a teacher by ID", response = TeacherResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> deleteTeacher(@PathVariable Long teacherId) {
        TeacherResponse teacherResponse = teacherService.deleteTeacher(teacherId);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherResponse);
    }

    @ApiOperation(value = "Update a teacher", response = TeacherResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping
    public ResponseEntity<TeacherResponse> updateTeacher(@RequestBody UpdateTeacherDTO teacher) {
        TeacherResponse teacherResponse = teacherService.updateTeacher(teacher);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherResponse);
    }

    @ApiOperation(value = "Get all teachers by campus name", response = TeacherListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teachers fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "No teachers found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/by-campus/{campusName}")
    public ResponseEntity<TeacherListResponse> getAllTeachersByCampusName(@PathVariable String campusName,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        TeacherListResponse teacherListResponse = teacherService.getAllTeachersByCampusName(campusName, page, size);
        return ResponseEntity.status(teacherListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherListResponse);
    }

    @ApiOperation(value = "Get all teachers", response = TeacherListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teachers fetched successfully"),
            @ApiResponse(code = 404, message = "No teachers found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<TeacherListResponse> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TeacherListResponse teacherListResponse = teacherService.getAllTeachers(page, size);
        return ResponseEntity.status(teacherListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherListResponse);
    }

    @ApiOperation(value = "Get a teacher by ID", response = TeacherWithSectionResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get/{email}")
    public ResponseEntity<TeacherWithSectionResponse> getTeacherById(@PathVariable String email) {
        TeacherWithSectionResponse teacherResponse = teacherService.getTeacherByEmail(email);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherResponse);
    }

    @ApiOperation(value = "Get a teacher sections by ID", response = TeacherSectionResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher sections fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherSectionResponse> getTeacherSectionById(@PathVariable Long teacherId) {
        TeacherSectionResponse teacherResponse = teacherService.getTeacherSection(teacherId);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherResponse);
    }

    @ApiOperation(value = "Get a teacher sections by course, grade or/and section", response = TeacherSectionResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher sections fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-teachers/{campusName}")
    public ResponseEntity<TeacherListResponse> getTeachersByCourseGradeSection(
            @PathVariable String campusName,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String section,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TeacherListResponse teacherResponse = teacherService.getTeachersByCourseGradeSection(campusName, course, grade, section, page, size);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherResponse);
    }

    @PostMapping("/upload-teacher-excel-data")
    public ResponseEntity<TeacherResponse> teacherCsvFileRead(@RequestParam("file") MultipartFile file) throws IOException {
        TeacherResponse teacherResponse = teacherService.saveTrachersFromFile(file);

        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 201 : 500)
                .body(teacherResponse);
    }

}
