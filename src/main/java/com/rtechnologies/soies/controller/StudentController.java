package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.dto.StudentIdsRequest;
import com.rtechnologies.soies.model.dto.StudentListResponse;
import com.rtechnologies.soies.model.dto.StudentListResponseDTO;
import com.rtechnologies.soies.model.dto.StudentResponse;
import com.rtechnologies.soies.service.StudentService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                        @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
                        @RequestParam(required = false, defaultValue = "10", name = "size") Integer size) {
                StudentListResponse studentListResponse = studentService.getAllStudentsByCampusName(campusName, page,
                                size);
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
                        @RequestParam(defaultValue = "10") int size) {
                StudentListResponse studentListResponse = studentService.getAllStudentsWithPagination(page, size);
                return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(studentListResponse);
        }

        @ApiOperation(value = "Get all students by grade section and course", response = StudentListResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Students retrieved successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @GetMapping("/get-students/{campusName}")
        public ResponseEntity<StudentListResponseDTO> getAllStudentsByGradeCourseSection(
                        @PathVariable String campusName,
                        @RequestParam(required = false) String course,
                        @RequestParam(required = false) String grade,
                        @RequestParam(required = false) String section,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                StudentListResponseDTO studentListResponse = studentService
                                .getAllStudentsByGradeCourseSection(campusName, course, grade, section, page, size);
                return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(studentListResponse);
        }

        @PostMapping("/upload-student-excel-data")
        public ResponseEntity<StudentListResponse> excelUploadFile(@RequestParam("file") MultipartFile file)
                        throws IOException {
                StudentListResponse response = studentService.saveStudentsFromFile(file);
                return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(response);
        }

        @GetMapping("/students/details")
        public ResponseEntity<Map<String, Object>> getStudentDetails(
                        @RequestParam String term,
                        @RequestParam String grade,
                        @RequestParam String section,
                        @RequestParam Long teacherId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Map<String, Object> response = studentService.getStudentDetailsForTeacher(teacherId,term, grade, section, page, size);

                if (response.get("studentDetails") instanceof List
                                && ((List<?>) response.get("studentDetails")).isEmpty()) {
                        return ResponseEntity.ok(Collections.singletonMap("studentDetails", Collections.emptyList()));
                }

                return ResponseEntity.ok(response);
        }

        @ApiOperation(value = "Get all students by roll number search", response = StudentListResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Students retrieved successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @GetMapping("/get-student-by-search")
        public ResponseEntity<StudentListResponse> getStudentBySearch(@RequestParam String rollNumber) {
                StudentListResponse studentListResponse = studentService.getStudentBySearch(rollNumber);
                return ResponseEntity.status(studentListResponse.getMessageStatus().equals("Success") ? 200 : 500)
                                .body(studentListResponse);
        }

        @ApiOperation(value = "Delete students by list of Ids", response = StudentListResponse.class)
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Students deleted successfully"),
                        @ApiResponse(code = 400, message = "Invalid request data"),
                        @ApiResponse(code = 500, message = "Internal server error")
        })
        @PostMapping("/delete-multiple-students")
        public ResponseEntity<StudentListResponse> deleteStudent(@RequestBody StudentIdsRequest studentIdsRequest) {
                StudentListResponse studentListResponse = studentService.deleteMultipleStudents(studentIdsRequest);
                return ResponseEntity
                                .status(studentListResponse.getMessageStatus().equals("Success") ? 200
                                                : (studentListResponse.getMessageStatus().equals("Not Found") ? 404
                                                                : 500))
                                .body(studentListResponse);
        }

}
