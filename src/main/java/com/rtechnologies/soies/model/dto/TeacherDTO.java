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

    private Long teacher_Id;
    private String Campus_Name;
    private String Employee_Name;
    private String email;
    private String Date_Of_Birth;
    private String gender;
    private String Joining_date;
    private String Phone_number;
    private String address;
    private String userName;
    private String grade;
    private List<String> sections;
    private List<String> courses;
    private List<TeacherSectionDTO> teacherSectionList;

    // Add additional fields for courses if needed

    // Constructors, getters, setters, and toString methods can be added as needed
}

