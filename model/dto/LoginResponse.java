package com.rtechnologies.soies.model.dto;

public class LoginResponse {
    private String token;
    private String userType; // Assuming it could be "teacher" or "student"
    private String username;

    // Constructors, getters, and setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

