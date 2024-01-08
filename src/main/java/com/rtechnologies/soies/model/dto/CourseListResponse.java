package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseListResponse {
    private List<Course> courseList;
    private String messageStatus;
}
