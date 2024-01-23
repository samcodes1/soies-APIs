package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.config.JwtConfig;
import com.rtechnologies.soies.model.dto.JwtAuthenticationResponse;
import com.rtechnologies.soies.model.dto.LoginRequest;
import com.rtechnologies.soies.service.CustomUserDetailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtConfig jwtTokenProvider;

    @Autowired
    private CustomUserDetailService customUserDetailsService;

    @PostMapping("/login")
    @ApiOperation(value = "Authenticate User", notes = "Authenticate user with username/email and password.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentication successful", response = JwtAuthenticationResponse.class),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public JwtAuthenticationResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login request: " + loginRequest.getUsernameOrEmail() + loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));


        System.out.println("Auth variable: " + authentication.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("After");
        String jwt = jwtTokenProvider.generateToken(authentication);
        System.out.println("jwt: "+jwt);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsernameOrEmail());

        if(userDetails != null) {
            return new JwtAuthenticationResponse(jwt);
        }
        return null;
    }

    @PostMapping("/logout")
    @ApiOperation(value = "Logout User", notes = "Logout currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Logout successful"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok("Logout successful");
    }
}
