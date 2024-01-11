package com.rtechnologies.soies.model.security;

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
    private final Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public CustomUserDetails(Teacher teacher) {
        this.username = teacher.getEmail();
        this.password = teacher.getPassword();
        this.authorities = Collections.singletonList(() -> "ROLE_TEACHER");
        this.userId = teacher.getTeacherId();
        this.userType = "TEACHER";
        // Add roles based on your application logic
        this.roles = Set.of("ROLE_TEACHER");

    }

    // Uncomment this part if you have a Student class
    /*
    public CustomUserDetails(Student student) {
        this.username = student.getUsername();
        this.password = student.getPassword();
        this.authorities = Collections.singletonList(() -> "ROLE_STUDENT");
        this.userId = student.getId();
        this.userType = "STUDENT";
        // Add roles based on your application logic
        this.roles = Set.of("ROLE_STUDENT");
    }
    */

    public Long getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
