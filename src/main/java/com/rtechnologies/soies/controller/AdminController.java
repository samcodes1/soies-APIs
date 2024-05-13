package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Admin;
import com.rtechnologies.soies.service.AdminService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    @ApiOperation("Create Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Admin created successfully", response = Admin.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 409, message = "Admin with given email already exists")
    })
    @PostMapping("/admin/create")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
      return  ResponseEntity.status(200)
              .body(adminService.createAdmin(admin));
    }
}
