package com.rtechnologies.soies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String rollNumber;
    private String password;
    private String studentName;
    private String gender;
    private String campusName;
    private String grade;
    private String sectionName;
    private String dateOfBirth;
    private String guardianName;
    private String guardianPhoneNumber;
    private String guardianEmail;
    private String address;
    private String city;
//    private String courses;

}
