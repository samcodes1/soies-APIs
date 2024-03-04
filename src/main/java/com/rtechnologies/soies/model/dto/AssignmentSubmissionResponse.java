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
public class AssignmentSubmissionResponse {
    private Long submissionId;
    private Long assignmentId; // ID of the Assignment associated with the submission
    private Long studentId; // ID of the Student submitting the assignment
    private String submissionDate;
    private String submittedFileURL;
    private String studentName;
    private String comments;
    private double obtainedMarks;
    private String grade;
    private String messageStatus;
}
