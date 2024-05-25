package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.association.TeacherSection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherWithSectionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teacherId;
    private String campusName;
    private String employeeName;
    private String email;
    private String dateOfBirth;
    private String gender;
    private String joiningDate;
    private String phoneNumber;
    private String address;
    private List<TeacherSection> teacherSections;
    private String messageStatus;
}
