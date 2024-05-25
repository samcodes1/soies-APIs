package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourseResponse {
    private long id;
    private Teacher teacher;
    private Course course;
    private String messageStatus;
}
