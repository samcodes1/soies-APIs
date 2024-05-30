package com.rtechnologies.soies.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface TeacherProjection {
    Long getTeacher_id();
    String getCampus_Name();
    String getEmployee_Name();
    String getEmail();
    String getPassword();
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
