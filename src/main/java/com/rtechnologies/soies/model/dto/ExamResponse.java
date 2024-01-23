package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {
    private Long assignmentId;
    private Course course;
    private String examTitle;
    private String description;
    private String file;
    private Date dueDate;
    private int totalMarks;
    private boolean visibility;
    private String messageStatus;
}
