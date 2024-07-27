package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.association.TeacherSection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeacherDTO {
    private Long teacherId;
    private String campusName;
    private String employeeName;
    private String email;
    private String password;
    private String dateOfBirth;
    private String gender;
    private String joiningDate;
    private String phoneNumber;
    private String address;
    private List<TeacherSection> teacherSectionList;
}