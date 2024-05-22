package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Dashboard")
@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/data/{teacherEmail}")
    @ApiOperation(value = "Get Dashboard Data by Teacher Email", notes = "Returns DashboardResponse based on teacher email.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved DashboardResponse"),
            @ApiResponse(code = 404, message = "Teacher not found with the provided email"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<?> getDashboardData(@PathVariable String teacherEmail) {
        return new ResponseEntity<>(dashboardService.getDashboardData(teacherEmail), HttpStatus.OK);
    }
}
