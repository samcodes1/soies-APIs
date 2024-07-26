package com.rtechnologies.soies.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface TeacherProjection {

    Long getTeacherId();
    String getTeacherName();
    // Other methods...
    Long getTeacher_id();
    String getCampus_Name();
    String getEmployee_Name();
    String getEmail();
    // String getPassword();
    String getDate_Of_Birth();
    String getGender();
    String getJoining_date();
    String getPhone_number();
    String getAddress();
    String getSection();
    String getGrade();
    String getCourse();
    String getCredits();
}
