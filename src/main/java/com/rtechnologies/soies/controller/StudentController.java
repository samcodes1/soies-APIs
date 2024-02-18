package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.dto.StudentListResponse;
import com.rtechnologies.soies.model.dto.StudentResponse;
import com.rtechnologies.soies.service.StudentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @ApiOperation(value = "Create a new student", response = StudentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Student created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/create-student")
    public ResponseEntity<StudentResponse> createStudent(@RequestBody Student student) {
        StudentResponse studentResponse = studentService.createStudent(student);
        return ResponseEntity.status(studentResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentResponse);
    }

    @ApiOperation(value = "Update an existing student", response = StudentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Student updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/update-student")
    public ResponseEntity<StudentResponse> updateStudent(@RequestBody Student student) {
        StudentResponse studentResponse = studentService.updateStudent(student);
        return ResponseEntity.status(studentResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentResponse);
    }

    @ApiOperation(value = "Delete a student by roll number", response = StudentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Student deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/delete-student/{rollNumber}")
    public ResponseEntity<StudentResponse> deleteStudent(@PathVariable String rollNumber) {
        StudentResponse studentResponse = studentService.deleteStudent(rollNumber);
        return ResponseEntity.status(studentResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentResponse);
    }

    @ApiOperation(value = "Get a student by roll number", response = StudentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Student retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-student/{rollNumber}")
    public ResponseEntity<StudentResponse> getStudentByRollNumber(@PathVariable String rollNumber) {
        StudentResponse studentResponse = studentService.getStudentByRollNumber(rollNumber);
        return ResponseEntity.status(studentResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentResponse);
    }

    @ApiOperation(value = "Get all students by campus name with pagination", response = StudentListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Students retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-all-students-by-campus/{campusName}")
    public ResponseEntity<StudentListResponse> getAllStudentsByCampusName(
            @PathVariable String campusName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        StudentListResponse studentListResponse = studentService.getAllStudentsByCampusName(campusName, page, size);
        return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentListResponse);
    }

    @ApiOperation(value = "Get all students with pagination", response = StudentListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Students retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-all-students")
    public ResponseEntity<StudentListResponse> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        StudentListResponse studentListResponse = studentService.getAllStudents(page, size);
        return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                .body(studentListResponse);
    }
}
