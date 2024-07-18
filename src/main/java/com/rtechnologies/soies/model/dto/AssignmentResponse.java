package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentResponse {
    private Long assignmentId;
    private Course course;
    private Teacher teacher;
    private String assignmentTitle;
    private String description;
    private String file;
    private String dueDate;
    private String section;
    private int totalMarks;
    private boolean visibility;
    private String term;
    private String messageStatus;
}
