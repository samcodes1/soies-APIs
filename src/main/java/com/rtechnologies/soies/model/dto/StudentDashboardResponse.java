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
public class StudentDashboardResponse {
    private String studentName;
    private List<Course> courseList;
    private String attendancePercentAge;
    private String termCompletion;
    private String messageStatus;
}
