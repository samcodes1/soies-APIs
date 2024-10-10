package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.service.OgaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oga")
public class OgaController {

    @Autowired
    private OgaService ogaService;

    @ApiOperation(value = "Create a new OGA", response = OgaResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<OgaResponse> createOga(@RequestBody CreateOgaRequest ogaRequest) {
        OgaResponse response = ogaService.createOga(ogaRequest);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "OGA Listing pagination", response = OgaResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<OgaResponse> ogaPaginationListing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OgaResponse response = ogaService.getPageListing(page, size);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Update an existing OGA", response = OgaResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 404, message = "OGA not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/update")
    public ResponseEntity<OgaResponse> updateOga(@RequestBody OgaRequest ogaRequest) {
        OgaResponse response = ogaService.updateOga(ogaRequest);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Delete an OGA", response = OgaResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA deleted successfully"),
            @ApiResponse(code = 404, message = "OGA not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/delete/{ogaId}")
    public ResponseEntity<OgaResponse> deleteOga(@PathVariable Long ogaId) {
        OgaResponse response = ogaService.deleteOga(ogaId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get an OGA by ID", response = OgaResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA retrieved successfully"),
            @ApiResponse(code = 404, message = "OGA not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get/{ogaId}")
    public ResponseEntity<OgaResponse> getOgaById(@PathVariable Long ogaId) {
        OgaResponse response = ogaService.getOgaById(ogaId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }

    @ApiOperation(value = "Get OGAs by course ID", response = OgaListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGAs retrieved successfully"),
            @ApiResponse(code = 404, message = "No OGAs found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-by-course/{courseId}/{studentRollNumber}")
    public ResponseEntity<OgaListResponse> getOgasByCourseId(@PathVariable Long courseId,
                                                             @PathVariable String studentRollNumber) {
        OgaListResponse response = ogaService.getOgasByCourseId(courseId, studentRollNumber);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Submit an OGA", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA submitted successfully"),
            @ApiResponse(code = 404, message = "No OGA found with the provided ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/submit")
    public ResponseEntity<String> submitOga(@RequestBody OgaSubmissionRequest ogaSubmissionRequest) {
        String response = ogaService.submitOga(ogaSubmissionRequest);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Get all submissions for a specific OGA", response = OgaSubmissionListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA submissions retrieved successfully"),
            @ApiResponse(code = 404, message = "No OGA found with the provided ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/submissions/{ogaId}")
    public ResponseEntity<OgaSubmissionListResponse> getAllOgaSubmission(@PathVariable Long ogaId) {
        OgaSubmissionListResponse response = ogaService.getAllOgaSubmission(ogaId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Get all For OGA by Course ID", response = OgaSubmissionListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGA submissions retrieved successfully"),
            @ApiResponse(code = 404, message = "No OGA found with the provided ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-oga/{courseid}")
    public ResponseEntity<OgaListResponse> getAllOgaByCourseId(
            @PathVariable Long courseid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OgaListResponse response = ogaService.getAllOgaByCourseid(courseid, page, size);
        return ResponseEntity.ok(response);
    }


    @ApiOperation(value = "Get submitted OGAs by course ID with pagination", response = OgaSubmissionListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OGAs retrieved successfully"),
            @ApiResponse(code = 404, message = "No OGAs found for the given course ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/get-oga-submissions/{courseId}")
    public ResponseEntity<OgaSubmissionListResponse> getOgaSubmissionsByCourseId(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page, // Default page number
            @RequestParam(defaultValue = "10") int size // Default page size
    ) {
        OgaSubmissionListResponse response = ogaService.getOgaSubmissionsByCourseId(courseId, page, size);
        return ResponseEntity.ok(response);
    }
}
