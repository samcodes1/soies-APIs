package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.TeacherListResponse;
import com.rtechnologies.soies.model.dto.TeacherResponse;
import com.rtechnologies.soies.service.TeacherService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('CREATE_TEACHER')")
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody Teacher teacher) {
        System.out.println("Done2");
        TeacherResponse teacherResponse = teacherService.createTeacher(teacher);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 201 : 500)
                .body(teacherResponse);
    }

    @PostMapping(produces = "application/json", path = "/hello")
    @PreAuthorize("hasAuthority('READ_TEACHER')")
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
    public ResponseEntity<TeacherResponse> updateTeacher(@RequestBody Teacher teacher) {
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
    public ResponseEntity<TeacherListResponse> getAllTeachersByCampusName(@PathVariable String campusName) {
        TeacherListResponse teacherListResponse = teacherService.getAllTeachersByCampusName(campusName);
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
    @PreAuthorize("hasAuthority('READ_TEACHER')")
    public ResponseEntity<TeacherListResponse> getAllTeachers() {
        TeacherListResponse teacherListResponse = teacherService.getAllTeachers();
        return ResponseEntity.status(teacherListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherListResponse);
    }

    @ApiOperation(value = "Get a teacher by ID", response = TeacherResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teacher fetched successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long teacherId) {
        TeacherResponse teacherResponse = teacherService.getTeacherById(teacherId);
        return ResponseEntity.status(teacherResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(teacherResponse);
    }
}
