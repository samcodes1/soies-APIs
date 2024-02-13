package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long studentId;
    private String rollNumber;
    private String studentName;
    private String gender;
    private String campusName;
    private String className;
    private String sectionName;
    private String dateOfBirth;
    private String guardianName;
    private String guardianPhoneNumber;
    private String guardianEmail;
    private String address;
    private String city;
    private String messageStatus;
}
