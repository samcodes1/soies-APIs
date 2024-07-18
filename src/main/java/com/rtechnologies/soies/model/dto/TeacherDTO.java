package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {

    private Long teacherId;
    private String campusName;
    private String employeeName;
    private String email;
    private String dateOfBirth;
    private String gender;
    private String joiningDate;
    private String phoneNumber;
    private String address;
    private String userName;
    private String grade;
    private List<String> sections;
    private List<String> courses;

    // Add additional fields for courses if needed

    // Constructors, getters, setters, and toString methods can be added as needed
}

