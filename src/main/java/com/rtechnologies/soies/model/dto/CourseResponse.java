package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Long courseId;
    private String courseName;
    private String description;
    private String grade;
    private int credits;
    private String messageStatus;
}
