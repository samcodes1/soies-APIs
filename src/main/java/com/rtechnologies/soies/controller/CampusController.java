package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.exception.SectionAlreadyExistsException;
import com.rtechnologies.soies.model.Campus;
import com.rtechnologies.soies.model.Section;
import com.rtechnologies.soies.service.CampusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/campuses")
@Api(value = "Campus Management System")
public class CampusController {

    @Autowired
    private CampusService campusService;

    @PostMapping
    @ApiOperation(value = "Create a new campus", response = Campus.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created campus"),
            @ApiResponse(code = 400, message = "Campus already exists")
    })
    public ResponseEntity<Campus> createCampus(@RequestBody Campus campus) {
        try {
            Campus savedCampus = campusService.createCampus(campus);
            return new ResponseEntity<>(savedCampus, HttpStatus.CREATED);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @ApiOperation(value = "Retrieve all campuses", response = List.class)
    public ResponseEntity<List<Campus>> getAllCampuses() {
        List<Campus> campuses = campusService.getAllCampuses(null);
        return ResponseEntity.ok(campuses);
    }

    @GetMapping("/{campusId}/{grade}")
    @ApiOperation(value = "Get sections by campus ID and grade", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "Campus not found")
    })
    public ResponseEntity<List<Section>> getSectionsByCampusNameAndGrade(@PathVariable Long campusId, @PathVariable String grade) {
        List<Section> sections = campusService.getSectionsByCampusNameAndGrade(campusId, grade);
        return ResponseEntity.ok(sections);
    }

    @PostMapping("/{campusId}/sections")
    @ApiOperation(value = "Create a section for a campus", response = Section.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created section"),
            @ApiResponse(code = 400, message = "Section already exists")
    })
    public ResponseEntity<String> createSection(@PathVariable Long campusId, @RequestBody Section section) {
        try {
            section.setCampusId(campusId); // Set the campus ID from path
            Section savedSection = campusService.createSection(section);
            return new ResponseEntity<>("Section created successfully.", HttpStatus.CREATED);
        } catch (SectionAlreadyExistsException ex) {
            // Return a more specific error message and status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            // Handle other runtime exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @ApiOperation(value = "Updates Campuses", response = Section.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created section"),
            @ApiResponse(code = 400, message = "Section already exists")
    })
    @PutMapping("/update-campus/{campusId}")
    public ResponseEntity<Campus> putMethodName(@PathVariable Long campusId, @RequestBody Campus campusUpdatedName) {
        try {
            Campus savedCampus = campusService.updateCampus(campusId, campusUpdatedName);
            return new ResponseEntity<>(savedCampus, HttpStatus.CREATED);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PutMapping("/sections-update/{sectionId}")
    @ApiOperation(value = "Update a section for a campus", response = Section.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created section"),
            @ApiResponse(code = 400, message = "Section already exists")
    })
    public ResponseEntity<Section> updateSection(@PathVariable Long sectionId, @RequestBody Section section) {
        try {
            Section savedSection = campusService.createSection(section);
            return new ResponseEntity<>(savedSection, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/get-campus")
    @ApiOperation(value = "getcampus data", response = Section.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created section"),
            @ApiResponse(code = 400, message = "Section already exists")
    })
    public ResponseEntity<List<Campus>> getCampusData(@RequestParam(required = false) Long campusid) {
        try {
            List<Campus> savedcampusdata = campusService.getAllCampuses(campusid);
            return ResponseEntity.ok(savedcampusdata);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/get-campus/{campusId}")
    @ApiOperation(value = "delete campus data", response = Section.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created section"),
            @ApiResponse(code = 400, message = "Section already exists")
    })
    public ResponseEntity<Campus> deleteCampusData(@PathVariable Long campusId) {
        try {
            Campus savedcampusdata = campusService.deleteCampuses(campusId);
            return ResponseEntity.ok(savedcampusdata);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/sections/{sectionId}")
    @ApiOperation(value = "Delete a section by ID", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted section"),
            @ApiResponse(code = 400, message = "Section not found")
    })
    public ResponseEntity<Map<String, String>> deleteSection(@PathVariable Long sectionId) {
        Map<String, String> response = new HashMap<>();
        try {
            campusService.deleteSection(sectionId);
            response.put("status", "200");
            response.put("message", "Successfully deleted section");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException ex) {
            response.put("status", "400");
            response.put("message", "Section not found");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

}
