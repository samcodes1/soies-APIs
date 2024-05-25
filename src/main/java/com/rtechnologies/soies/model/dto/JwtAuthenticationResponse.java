package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Admin;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Collection<? extends GrantedAuthority> authorities;
    private String role;
    private Teacher teacher;
    private Student student;
    private Admin admin;
    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String accessToken, Collection<? extends GrantedAuthority> authorities, String role,
                                     Student student, Teacher teacher, Admin admin) {
        this.accessToken = accessToken;
        this.authorities = authorities;
        this.role = role;
        this.student = student;
        this.teacher = teacher;
        this.admin = admin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}
