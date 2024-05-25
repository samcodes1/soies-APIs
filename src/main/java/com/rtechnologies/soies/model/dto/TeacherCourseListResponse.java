package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.association.TeacherCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourseListResponse {
    private List<TeacherCourse> teacherCourses;
    private String messageStatus;
}
