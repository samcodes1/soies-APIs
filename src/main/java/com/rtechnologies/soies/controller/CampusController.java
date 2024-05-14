package com.rtechnologies.soies.controller;

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

import java.util.List;

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
        List<Campus> campuses = campusService.getAllCampuses();
        return ResponseEntity.ok(campuses);
    }

    @GetMapping("/{campusId}/sections")
    @ApiOperation(value = "Get sections by campus ID and grade", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "Campus not found")
    })
    public ResponseEntity<List<Section>> getSectionsByCampusNameAndGrade(@PathVariable Long campusId, @RequestParam String grade) {
            List<Section> sections = campusService.getSectionsByCampusNameAndGrade(campusId, grade);
            return ResponseEntity.ok(sections);
    }

    @PostMapping("/{campusId}/sections")
    @ApiOperation(value = "Create a section for a campus", response = Section.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created section"),
            @ApiResponse(code = 400, message = "Section already exists")
    })
    public ResponseEntity<Section> createSection(@PathVariable Long campusId, @RequestBody Section section) {
        try {
            section.setCampusId(campusId); // Set the campus ID from path
            Section savedSection = campusService.createSection(section);
            return new ResponseEntity<>(savedSection, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
