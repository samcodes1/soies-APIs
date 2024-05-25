package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.config.JwtConfig;
import com.rtechnologies.soies.model.Admin;
import com.rtechnologies.soies.model.dto.JwtAuthenticationResponse;
import com.rtechnologies.soies.model.dto.LoginRequest;
import com.rtechnologies.soies.model.security.CustomUserDetails;
import com.rtechnologies.soies.repository.AdminRepository;
import com.rtechnologies.soies.service.CustomUserDetailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtConfig jwtTokenProvider;

    @Autowired
    private CustomUserDetailService customUserDetailsService;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    @ApiOperation(value = "Authenticate User", notes = "Authenticate user with username/email and password.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentication successful", response = JwtAuthenticationResponse.class),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public JwtAuthenticationResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        //        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsernameOrEmail());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            return new JwtAuthenticationResponse(jwt, authorities,"ROLE_TEACHER", userDetails.getStudent(), userDetails.getTeacher(), userDetails.getAdmin());

        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            return new JwtAuthenticationResponse(jwt,authorities, "ROLE_STUDENT", userDetails.getStudent(), userDetails.getTeacher(), userDetails.getAdmin());
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new JwtAuthenticationResponse(jwt,authorities, "ROLE_ADMIN", userDetails.getStudent(), userDetails.getTeacher(), userDetails.getAdmin());
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
