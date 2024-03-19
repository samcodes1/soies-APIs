package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.dto.CreateOgaRequest;
import com.rtechnologies.soies.model.dto.OgaListResponse;
import com.rtechnologies.soies.model.dto.OgaRequest;
import com.rtechnologies.soies.model.dto.OgaResponse;
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
    @GetMapping("/get-by-course/{courseId}")
    public ResponseEntity<OgaListResponse> getOgasByCourseId(@PathVariable Long courseId) {
        OgaListResponse response = ogaService.getOgasByCourseId(courseId);
        return ResponseEntity.status(response.getMessageStatus().equals("Success") ? 200 : 500)
                .body(response);
    }
}
