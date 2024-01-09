package com.rtechnologies.soies.model.security;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Additional fields depending on your user entity
    private final Long userId;
    private final String userType;  // For example, "TEACHER" or "STUDENT"

    private Set<String> roles;

    // Other fields and methods...

    public Set<String> getRoles() {
        return roles;
    }
    public CustomUserDetails(Teacher teacher) {
        this.username = teacher.getEmail();
        this.password = teacher.getPassword();
        this.authorities = Collections.singletonList(() -> "ROLE_TEACHER");
        this.userId = teacher.getTeacherId();
        this.userType = "TEACHER";
    }

//    public CustomUserDetails(Student student) {
//        this.username = student.getUsername();
//        this.password = student.getPassword();
//        this.authorities = Collections.singletonList(() -> "ROLE_STUDENT");
//        this.userId = student.getId();
//        this.userType = "STUDENT";
//    }

    public Long getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    // Implement other UserDetails methods...

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    // Other UserDetails methods...
}

