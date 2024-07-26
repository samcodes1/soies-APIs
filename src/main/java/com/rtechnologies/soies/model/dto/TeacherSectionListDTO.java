package com.rtechnologies.soies.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSectionListDTO {
    private Long teacherId;
    private String teacherName;
    private String campusName;
    private String employeeName;
    private String email;
    private String dateOfBirth;
    private String gender;
    private String joiningDate;
    private String phoneNumber;
    private String address;
    private String section;
    private String grade;
    private String course;
    private String credits;
    private List<TeacherSectionDTO> teacherSectionList;

}
