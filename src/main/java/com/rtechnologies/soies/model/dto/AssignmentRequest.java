package com.rtechnologies.soies.model.dto;

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
public class AssignmentRequest {
    private Long assignmentId;
    private Long courseId;
    private Long teacherId; // ID of the Teacher associated with the assignment
    private String assignmentTitle;
    private String description;
    private MultipartFile file;
    private int totalMarks;
    private String term;
    private boolean visibility;
}
